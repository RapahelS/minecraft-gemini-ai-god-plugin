package net.bigyous.gptgodmc.GPT.Json;



// https://ai.google.dev/api/caching#Content
public class Content {
    public enum Role {
        user,
        model
    }

    private Part[] parts;
    private Role role;

    public Content() {
        role = Role.user;
    }

    public Content(Part[] parts) {
        this.parts = parts;
        role = Role.user;
    }

    public Content(String message) {
        this.role = Role.user;
        this.parts = new Part[] {new Part(message)};
    }

    public Content(Role role, String message) {
        this.role = role;
        this.parts = new Part[] {new Part(message)};
    }

    public Part[] getParts() {
        return parts;
    }
    public void setParts(Part[] parts) {
        this.parts = parts;
    }
    public Role getRole() {
        return role;
    }
}