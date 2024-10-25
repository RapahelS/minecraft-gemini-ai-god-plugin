package net.bigyous.gptgodmc.GPT.Json;

import java.util.ArrayList;

// https://ai.google.dev/api/caching#Content
public class Content {
    public enum Role {
        user,
        model
    }

    private ArrayList<Part> parts = new ArrayList<>();
    private Role role;

    public Content() {
        role = Role.user;
    }

    public Content(ArrayList<Part> parts) {
        this.parts = parts;
        role = Role.user;
    }

    public Content(String message) {
        this.role = Role.user;
        this.parts = new ArrayList<Part>();
        this.parts.add(new Part(message));
    }

    public Content(Role role, String message) {
        this.role = role;
        this.parts = new ArrayList<Part>();
        this.parts.add(new Part(message));
    }

    public ArrayList<Part> getParts() {
        return parts;
    }
    public void setParts(ArrayList<Part> parts) {
        this.parts = parts;
    }
    public Role getRole() {
        return role;
    }
}