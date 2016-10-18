import java.util.regex.*;

/**
 * Created by zxx_1 on 2016/9/18.
 */
public class CommandRecognizer {
    public CommandRecognizer() {

    }
    public String operand;                                                  // ��������в�֣��õ���������inputType�������operand
    public InputType inputType;
    private String inputString;
    public void recognise(String inputString) {
        this.inputString = inputString;
        if (isEnd()) {
            operand = "";
            inputType = InputType.End;
            return;
        }
        if (isSimplification()) {
            operand = inputString.substring(10, inputString.length());
            inputType = InputType.Simplification;
            return;
        }
        if (isDerivation()) {
            operand = inputString.substring(5, inputString.length());
            inputType = InputType.Derivation;
            return;
        }
        if (isExpression()) {
            operand = inputString;
            inputType = InputType.Expression;
            return;
        }
        operand = "";
        inputType = InputType.Unrecognised;
        return;

    }

    private boolean matchPattern(String pattern) {                                      //��inputString����ָ��������ʽ��ƥ����
        Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = regex.matcher(inputString);
        return matcher.matches();
    }

    private boolean isEnd() {
        return matchPattern("^!End");
    }

    private boolean isSimplification() {
        return matchPattern("^!Simplify [\\w|\\d|\\s|=|,]+");
    }

    private boolean isDerivation() {
        return matchPattern("^!d/d\\s+[a-zA-Z]+\\s*$");
    }

    private boolean isExpression() {
        return matchPattern("[\\w|\\d|\\s|\\-|+|*|^|(|)]+");
    }
}
