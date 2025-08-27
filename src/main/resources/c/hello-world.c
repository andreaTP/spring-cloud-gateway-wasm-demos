#include <stdio.h>
#include <string.h>

int main() {
    char input[1024];

    if (fgets(input, sizeof(input), stdin) != NULL) {
        input[strcspn(input, "\n")] = 0;
        printf("Hello %s\n", input);
    } else {
        printf("Hello World!\n");
    }
    
    return 0;
}
