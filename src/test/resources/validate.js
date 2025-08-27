function validate(userStr) {
    const user = JSON.parse(userStr);
    const errors = [];
    if (!user.name || user.name.length < 3) {
        errors.push("Name must be at least 3 characters.");
    }
    if (user.age < 18) {
        errors.push("User must be 18 or older.");
    }
    return errors.length > 0 ? errors.join(" ") : "Valid";
}
