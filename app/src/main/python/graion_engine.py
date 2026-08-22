"""
Graion Scientific & Engineering Python Engine
Provides runtime symbolic solving, engineering wire-drawing models, and custom script execution.
"""
import io
import sys
import math
import traceback

def execute_script(code: str) -> str:
    """
    Executes a snippet of Python code and captures output or expression evaluation.
    """
    code = code.strip()
    if not code:
        return ""

    # Redirect stdout to capture prints
    old_stdout = sys.stdout
    redirected_output = io.StringIO()
    sys.stdout = redirected_output

    scope = {
        "math": math,
        "pi": math.pi,
        "e": math.e,
        "sqrt": math.sqrt,
        "sin": math.sin,
        "cos": math.cos,
        "tan": math.tan,
        "log": math.log,
        "log10": math.log10,
        "exp": math.exp
    }

    try:
        # Try evaluating as single expression first (e.g. "math.sqrt(144) + 5")
        try:
            compiled_expr = compile(code, "<string>", "eval")
            eval_result = eval(compiled_expr, scope)
            sys.stdout = old_stdout
            stdout_val = redirected_output.getvalue().strip()
            if stdout_val:
                return f"{stdout_val}\n=> {eval_result}"
            return str(eval_result)
        except SyntaxError:
            # If not an expression, execute as statements
            compiled_stmt = compile(code, "<string>", "exec")
            exec(compiled_stmt, scope)
            sys.stdout = old_stdout
            stdout_val = redirected_output.getvalue().strip()
            return stdout_val if stdout_val else "(Executed successfully with no output)"
    except Exception:
        sys.stdout = old_stdout
        return f"Error: {traceback.format_exc().splitlines()[-1]}"

def calculate_die_series(starting_die: float, final_die: float, num_passes: int) -> list:
    """
    Generates an optimized wire drawing die series using constant elongation.
    """
    if starting_die <= final_die or num_passes <= 0:
        return [starting_die]

    ratio = (final_die / starting_die) ** (1.0 / num_passes)
    series = [round(starting_die, 3)]
    current = starting_die

    for _ in range(num_passes):
        current *= ratio
        series.append(round(current, 3))

    return series
