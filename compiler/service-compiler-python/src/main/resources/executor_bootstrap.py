import sys
import json
import io
import time
from datetime import datetime
import traceback

def execute_user_code():
    # 1. Read the entire input (JSON) from stdin
    try:
        input_data = json.load(sys.stdin)
        user_code = input_data.get("code", "")
        argument_sets = input_data.get("arguments", [])
    except json.JSONDecodeError as e:
        print(json.dumps([{
            "errors": [{"type": "InputError", "message": f"Invalid JSON input: {e}"}]
        }]), file=sys.stdout, flush=True)
        return

    # 2. Define the user's function in the global scope
    # We need to extract the function name to call it later
    try:
        # Simple assumption: the function name is the first word after "def"
        func_name = user_code.split("def ")[1].split("(")[0].strip()
        
        # Execute the code to define the function
        exec(user_code, globals())
    except Exception as e:
        print(json.dumps([{
            "errors": [{"type": "DefinitionError", "message": f"Could not define function: {e}"}]
        }]), file=sys.stdout, flush=True)
        return

    results = []
    
    # 3. Iterate through each argument set and execute the function
    for args in argument_sets:
        # Redirect stdout to capture logs
        old_stdout = sys.stdout
        redirected_output = io.StringIO()
        sys.stdout = redirected_output

        start_time = time.perf_counter()
        output = None
        errors = []

        try:
            # Call the function with the arguments
            # The *args unpacks the list into individual arguments
            output = globals()[func_name](*args)
        except Exception:
            # Catch all runtime errors
            exc_type, exc_value, exc_tb = sys.exc_info()
            error_message = "".join(traceback.format_exception_only(exc_type, exc_value)).strip()
            errors.append({"type": exc_type.__name__, "message": error_message})
        
        end_time = time.perf_counter()
        run_time_ms = int((end_time - start_time) * 1000)

        # Restore stdout
        sys.stdout = old_stdout
        
        # Get collected logs from the redirect
        logs = redirected_output.getvalue().splitlines()

        results.append({
            "input": args,
            "output": output,
            "logs": logs,
            "errors": errors,
            "runTimeMs": run_time_ms
        })

    # 4. Output all results as a JSON array to stdout
    print(json.dumps(results), flush=True)

if __name__ == '__main__':
    execute_user_code()
