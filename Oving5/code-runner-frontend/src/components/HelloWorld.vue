<template>
  <textarea v-model="code" />
  <p>{{ output }}</p>
  <button @click="sendCode">submit</button>
</template>

<script>
import axios from "axios";

export default {
  name: "HelloWorld",
  props: {
    msg: String,
  },
  data() {
    return {
      code: "class Main{\n\tpublic static void main(String[] args) {\n\n\t}\n}",
      output: "",
    };
  },
  methods: {
    async sendCode() {
      const response = await axios.post("http://localhost:5001/run", {
        code: this.code,
      });
      console.log(response.data);
      this.output = response.data;
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}

textarea {
  height: 100px;
  width: 400px;
}
</style>
