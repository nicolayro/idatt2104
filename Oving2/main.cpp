#include <iostream>
#include <list>
#include <vector>
#include <functional>
#include <thread>
#include <condition_variable>

class Workers {
private:
    int num_threads;
    bool wait = true;
    bool done = false;
    std::vector<std::thread> threads;
    std::list<std::function<void()>> tasks;
    std::mutex wait_mutex;
    std::condition_variable cv;
public:

    Workers(int num_threads1) {
        num_threads = num_threads1;
    }

    void start() {
        for(int i = 0; i < num_threads; i++) {
            threads.emplace_back([&]  {
                while(!done) {

                    std::function<void()> task;
                    {
                        std::unique_lock<std::mutex> lock(wait_mutex);
                        while (wait) cv.wait(lock);

                        if(!tasks.empty()) {
                            task = *tasks.begin();
                            tasks.pop_front();
                        }
                    }
                    if (task) {
                        task(); // Run task outside of mutex lock
                    } else {
                        // Ends loop if there are no tasks
                        stop();
                    }
                    // std::cout << "thread: " << std::this_thread::get_id() << " finished waiting" << std::endl;
                }
            });
        }
    }

    void post(const std::function<void()> &func) {
        tasks.emplace_back(func);
        {
                std::unique_lock<std::mutex> lock(wait_mutex);
                wait = false;
        }
        cv.notify_all();
    };

    void join() {
        for(auto &thread : threads) {
            thread.join();
        }
    }

    void stop() {
        done = true;
     }

    void post_timeout(const std::function<void()> &func, long timeout) {
        std::this_thread::sleep_for(std::chrono::milliseconds(timeout));
        post(func);
    }


};

// Tasks
void a() {
    std::cout << "task a" << std::endl;
}
void b() {
    std::cout << "task b" << std::endl;
}
void c() {
    std::cout << "task c" << std::endl;
}
void d() {
    std::cout << "task d" << std::endl;
}


// Demos
void post_demo() {
    Workers worker_threads(4);
    Workers event_loop(1);

    // Starting workers
    worker_threads.start();
    event_loop.start();

    // Posting tasks to worker_threads
    worker_threads.post([&] {
        a();
    });
    worker_threads.post([&] {
        b();
    });

    // Posting tasks to event_loop
    event_loop.post([&] {
        c();
    });
    event_loop.post([&] {
        d();
    });

    worker_threads.join();
    event_loop.join();
}

void post_timeout_demo() {
    Workers event_loop(1);
    event_loop.start();
    event_loop.post_timeout([] {
        a();
    }, 2000); // Call task after 2000ms
    event_loop.post_timeout([] {
        a();
    }, 1000); // Call task after 1000ms
    event_loop.join();
};


int main() {
    //post_demo();
    post_timeout_demo();
    return 0;
}
