#include <iostream>

class Dog {
private:
  std::string color;

public:
  Dog(){};

  void bark() {
    std::cout << "Woof!" << std::endl;
  }
};

int main() {

  Dog dog;
  dog.bark();

  std::cout << "Hello, world!\n";
  return 0;
}
