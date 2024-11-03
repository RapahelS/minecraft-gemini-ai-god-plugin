package net.bigyous.gptgodmc.GPT.Json;

public class Tool {
    /*
     * Optional. A list of FunctionDeclarations available to the model that can be
     * used for function calling. The model or system does not execute the function.
     * Instead the defined function may be returned as a FunctionCall with arguments
     * to the client side for execution. The model may decide to call a subset of
     * these functions by populating FunctionCall in the response. The next
     * conversation turn may contain a FunctionResponse with the Content.role
     * "function" generation context for the next model turn.
     */
    private FunctionDeclaration[] functionDeclarations;

    // codeExecution object (CodeExecution)
    // Optional. Enables the model to execute code as part of generation.
    // private CodeExecution codeExecution;

    public Tool(FunctionDeclaration[] functionDeclarations) {
        this.functionDeclarations = functionDeclarations;
    }

    public Tool(FunctionDeclaration functionDeclaration) {
        this.functionDeclarations = new FunctionDeclaration[] { functionDeclaration };
    }

    public FunctionDeclaration[] getFunctions() {
        return functionDeclarations;
    }
}
