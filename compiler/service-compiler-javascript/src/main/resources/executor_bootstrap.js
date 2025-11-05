const util = require('util');

// Read all input from stdin asynchronously
function readStdin() {
    return new Promise((resolve) => {
        let data = '';
        process.stdin.on('readable', () => {
            let chunk;
            while ((chunk = process.stdin.read()) !== null) {
                data += chunk;
            }
        });
        process.stdin.on('end', () => {
            resolve(data);
        });
    });
}

// Extract function name from code
function extractFunctionName(code) {
    // Handle function declarations: function name() {...}
    const funcDeclMatch = code.match(/function\s+([a-zA-Z_$][0-9a-zA-Z_$]*)\s*\(/);
    if (funcDeclMatch) {
        return funcDeclMatch[1];
    }
    
    // Handle arrow functions assigned to const/let/var: const name = () => {...}
    const arrowFuncMatch = code.match(/(?:const|let|var)\s+([a-zA-Z_$][0-9a-zA-Z_$]*)\s*=/);
    if (arrowFuncMatch) {
        return arrowFuncMatch[1];
    }
    
    return null;
}

// Main function that controls everything
async function main() {
    // 1. Read JSON input from Java
    const jsonInput = await readStdin();
    let payload;
    try {
        payload = JSON.parse(jsonInput);
    } catch (e) {
        // If input JSON is invalid, send back an error
        const errorResult = [{ errors: [{ type: 'InputError', message: `Invalid JSON input: ${e.message}` }] }];
        console.log(JSON.stringify(errorResult));
        return;
    }

    const { code: userCode, arguments: argumentSets } = payload;
    const results = [];
    
    // 2. Create a new VM context to run the user code
    const vm = require('vm');
    const context = {
        console: {
            log: (...args) => {}
        },
        require: (module) => {
            throw new Error(`Require not allowed: ${module}`);
        },
        process: {
            env: {},
            exit: () => {}
        }
    };
    
    // 3. Execute the user code in the sandbox
    try {
        // First, try to run the code to define the function
        vm.createContext(context);
        vm.runInContext(userCode, context);
        
        // Extract the function name
        const funcName = extractFunctionName(userCode);
        if (!funcName) {
            throw new Error('Could not determine function name from code');
        }
        
        const userFunction = context[funcName];
        if (typeof userFunction !== 'function') {
            throw new Error(`'${funcName}' is not a function`);
        }
        
        // 4. Execute the function with each set of arguments
        for (const args of argumentSets) {
            const logs = [];
            const errors = [];
            let output = null;
            
            // Override console.log to capture output
            context.console.log = (...logArgs) => {
                logs.push(util.format(...logArgs));
            };
            
            const startTime = process.hrtime.bigint();
            try {
                // Call the user function with arguments
                output = userFunction.apply(context, args);
                
                // Handle async functions (Promises)
                if (output && typeof output.then === 'function') {
                    return output.then(async (result) => {
                        const endTime = process.hrtime.bigint();
                        const runTimeMs = Number(endTime - startTime) / 1_000_000;
                        
                        results.push({
                            input: args,
                            output: result,
                            logs: logs,
                            errors: errors,
                            runTimeMs: Math.round(runTimeMs)
                        });
                        
                        // Output results and exit
                        console.log(JSON.stringify(results, null, 2));
                        process.exit(0);
                    }).catch(error => {
                        errors.push({ 
                            type: error.name || 'Error', 
                            message: error.message || 'An unknown error occurred in async function' 
                        });
                        
                        const endTime = process.hrtime.bigint();
                        const runTimeMs = Number(endTime - startTime) / 1_000_000;
                        
                        results.push({
                            input: args,
                            output: null,
                            logs: logs,
                            errors: errors,
                            runTimeMs: Math.round(runTimeMs)
                        });
                        
                        // Output results and exit
                        console.log(JSON.stringify(results, null, 2));
                        process.exit(0);
                    });
                }
                
                const endTime = process.hrtime.bigint();
                const runTimeMs = Number(endTime - startTime) / 1_000_000;
                
                results.push({
                    input: args,
                    output: output,
                    logs: logs,
                    errors: errors,
                    runTimeMs: Math.round(runTimeMs)
                });
                
            } catch (e) {
                // Catch runtime errors
                errors.push({ 
                    type: e.name || 'Error', 
                    message: e.message || 'An unknown error occurred' 
                });
                
                const endTime = process.hrtime.bigint();
                const runTimeMs = Number(endTime - startTime) / 1_000_000;
                
                results.push({
                    input: args,
                    output: null,
                    logs: logs,
                    errors: errors,
                    runTimeMs: Math.round(runTimeMs)
                });
            }
        }
        
    } catch (e) {
        // Handle any errors during function definition or execution
        results.push({
            input: [],
            output: null,
            logs: [],
            errors: [{
                type: e.name || 'Error',
                message: e.message || 'An error occurred during execution'
            }],
            runTimeMs: 0
        });
    }
    
    // 5. Output all results as a single JSON array to stdout
    console.log(JSON.stringify(results, null, 2));
}

// Start the execution
main().catch(error => {
    console.error('Unhandled error:', error);
    process.exit(1);
});
