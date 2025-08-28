package main

import (
	"encoding/json"
	"fmt"
	"unsafe"
	"strings"
)

// #include <stdlib.h>
// #include <string.h>
import "C"

type User struct {
	Name   string   `json:"name"`
	Age    int      `json:"age"`
	Skills []string `json:"skills"`
}

//export validate
func validate(userStr *byte) *byte {
	goStr := C.GoString((*C.char)(unsafe.Pointer(userStr)))
	var user User
	err := json.Unmarshal([]byte(goStr), &user)
	if err != nil {
		fmt.Printf("DEBUG: JSON unmarshal error: %v\n", err)
		return nil
	}

	var errors []string
	
	if user.Name == "" || len(user.Name) < 3 {
		errors = append(errors, "Name must be at least 3 characters.")
	}
	
	if user.Age < 18 {
		errors = append(errors, "User must be 18 or older.")
	}

	var result string
	if len(errors) > 0 {
		result = strings.Join(errors, " ")
	} else {
		result = "Valid"
	}
	
	return (*byte)(unsafe.Pointer(C.CString(result)))
}

func main() {
	// This function is required
}
