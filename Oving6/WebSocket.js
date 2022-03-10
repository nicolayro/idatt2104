const net = require("net");
const crypto = require("crypto");
const { stringify } = require("querystring");

function createResponsekey(acceptKey) {
  return crypto
    .createHash("sha1")
    .update(acceptKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11", "binary")
    .digest("base64");
}

// Simple HTTP server responds with a simple WebSocket client test
const httpServer = net.createServer((connection) => {
  connection.on("data", () => {
    let content = `<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
  </head>
  <body>
    WebSocket test page
    <script>
      let ws = new WebSocket('ws://localhost:3001');
      ws.onmessage = event => alert('Message from server: ' + event.data);
      ws.onopen = () => ws.send('hello');
    </script>
  </body>
</html>
`;
    connection.write(
      "HTTP/1.1 200 OK\r\nContent-Length: " +
        content.length +
        "\r\n\r\n" +
        content
    );
  });
});

httpServer.listen(3000, () => {
  console.log("HTTP server listening on port 3000");
});

// Incomplete WebSocket server
const wsServer = net.createServer((connection) => {
  console.log("Client connected");

  connection.on("upgrade", () => {
    console.log("yoyo");
  });

  connection.on("data", (data) => {
    if (data.toString().search("GET") !== -1) {
      const acceptKey = findKey(data.toString());
      const hash = createResponsekey(acceptKey);
      const responseHeaders = [
        "HTTP/1.1 101 Web Socket Protocol Handshake",
        "Upgrade: WebSocket",
        "Connection: Upgrade",
        `Sec-WebSocket-Accept: ${hash}`,
      ];
      connection.write(responseHeaders.join("\r\n") + "\r\n\r\n");
    } else {
      const message = parseMessage(data);
      if (message) {
        console.log("Message from client: %s", message);
        connection.write(constructReply("Hello from server!"));
      }
    }
  });

  connection.on("end", () => {
    console.log("Client disconnected");
  });
});

wsServer.on("error", (error) => {
  console.error("Error: ", error);
});

wsServer.listen(3001, () => {
  console.log("WebSocket server listening on port 3001");
});

function findKey(handshake) {
  const pos = handshake.search("Sec-WebSocket-Key");
  if (pos === -1) {
    return;
  }
  const key = handshake.substring(pos + 19, pos + 43);
  return key;
}

function constructReply(data) {
  const jsonByteLength = Buffer.byteLength(data);
  const lengthByteCount = jsonByteLength < 126 ? 0 : 2;
  const payloadLength = lengthByteCount === 0 ? jsonByteLength : 126;
  const buffer = Buffer.alloc(2 + lengthByteCount + jsonByteLength);
  buffer.writeUInt8(0b10000001, 0);
  buffer.writeUInt8(payloadLength, 1);
  let payloadOffset = 2;
  if (lengthByteCount > 0) {
    buffer.writeUInt16BE(jsonByteLength, 2);
    payloadOffset += lengthByteCount;
  }
  buffer.write(data, payloadOffset);
  return buffer;
}

function parseMessage(buffer) {
  const firstByte = buffer.readUInt8(0);
  const isFinalFrame = Boolean((firstByte >>> 7) & 0x1);
  const [reserved1, reserved2, reserved3] = [
    Boolean((firstByte >>> 6) & 0x1),
    Boolean((firstByte >>> 5) & 0x1),
    Boolean((firstByte >>> 4) & 0x1),
  ];
  const opCode = firstByte & 0xf;
  if (opCode === 0x8) return null;
  if (opCode !== 0x1) return;
  const secondByte = buffer.readUInt8(1);
  const isMasked = Boolean((secondByte >>> 7) & 0x1);
  let currentOffset = 2;
  let payloadLength = secondByte & 0x7f;
  if (payloadLength > 125) {
    if (payloadLength === 126) {
      payloadLength = buffer.readUInt16BE(currentOffset);
      currentOffset += 2;
    }
  }

  let maskingKey;
  if (isMasked) {
    maskingKey = buffer.readUInt32BE(currentOffset);
    currentOffset += 4;
  }

  const data = Buffer.alloc(payloadLength);
  if (isMasked) {
    for (let i = 0, j = 0; i < payloadLength; ++i, j = i % 4) {
      const shift = j == 3 ? 0 : (3 - j) << 3;
      const mask = (shift == 0 ? maskingKey : maskingKey >>> shift) & 0xff;
      const source = buffer.readUInt8(currentOffset++);
      data.writeUInt8(mask ^ source, i);
    }
  } else {
    buffer.copy(data, 0, currentOffset++);
  }
  const message = data.toString("utf8");
  return message;
}
