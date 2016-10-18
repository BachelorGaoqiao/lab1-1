import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zxx_1 on 2016/9/18.
 */
public class Expression {
    public Expression() {
        variableList = new HashMap<String, Integer>();
        compileResults = new ArrayList<NumericTerm>();
        compiledMark = false;
    }
    public void compile(String expression) throws ExpressionCompileException{
        String innerString;
        innerString = blankStrip(expression);
        innerString = completeMultiplication(innerString);
        try {
            innerString = replacePowerNotion(innerString);
            reduceBracket(innerString);
            generateVariableList();
            transformIntoNumeric();
            mergeResults();
        } catch(ExpressionCompileException e) {
            throw e;
        }
        compiledMark = true;
    }
    public String toString() {                                              // ��numericTerm��ʽ�洢�Ķ���ʽת��Ϊ�ַ������
        String result = transformNumericTermToString(compileResults.get(0));
        for(int i = 1; i < compileResults.size(); i++) {
            String termString = transformNumericTermToString(compileResults.get(i));
            if (!termString.startsWith("-"))                                // ����ǰû�и��ţ�����Ҫ�������
                result += "+";
            result += termString;
        }
        return result;
    }
    public boolean isCompiled() {
        return compiledMark;
    }
    public boolean hasVariable() {
        // �����
    }
    public String derivate(String variable) {
        // �����
    }
    public String simplify(String assignments) {
        // �����
    }
    private boolean compiledMark;
    private ArrayList<Term> resultTerms;
    private HashMap<String, Integer> variableList;
    private int variableNumber;
    private ArrayList<NumericTerm> compileResults;
    private ArrayList<String> variableIndexToName;
    public static final double numericError = 1e-6;
    private String blankStrip(String expression) {
        return expression.replaceAll("\\s+", "");                           // ��ȥ�����еĿո�
    }
    private String completeMultiplication(String expression) {
        String innerString = expression.replaceAll("([\\)|\\d])([\\(||a-z||A-Z])", "$1*$2");
        return innerString.replaceAll("([a-zA-Z])(\\()", "$1*$2");
    }
    private String replacePowerNotion(String expression) throws ExpressionCompileException{
        PowerNotationReplacer powerNotationReplacer = new PowerNotationReplacer(expression);
        return powerNotationReplacer.getResult();
    }
    private void reduceBracket(String expression) throws ExpressionCompileException {
        BracketReducer bracketReducer = new BracketReducer(expression);
        resultTerms = bracketReducer.resultTerms;
    }
    private void generateVariableList() {
        variableIndexToName = new ArrayList<>();
        for(Term term : resultTerms)                                        // ��ÿһ��
            for(String fragments : term.content.split("\\*"))               // �ó˺ŷָ���
                if (fragments.matches("[a-zA-Z]+") == true && variableIndexToName.contains(fragments) == false)
                    variableIndexToName.add(fragments);                     // �����δ���ֹ��Ĵ���ĸ�ִ�������뵽�������б�
        for(int i = 0; i < variableIndexToName.size(); i++)
            variableList.put(variableIndexToName.get(i), i);                // ����ִ����ʵ�
        variableNumber = variableIndexToName.size();
    }
    private void transformIntoNumeric() throws ExpressionCompileException{
        for(Term term : resultTerms) {
            ArrayList<Integer> powers = new ArrayList<Integer>();
            for(int i = 0; i < variableNumber; i++)
                powers.add(0);                                               // �ݼ�����ʼ��
            double coefficient = 1.0d;                                          // ϵ����ʼ�������ڸ����Ϊ-1
            if (term.sign == '\0')
                coefficient *= -1;
            for(String fragments : term.content.split("\\*")) {                 // ���ڳ˺ŷָ�����ÿһ����
                if (fragments.matches("[a-zA-Z]+")) {
                    int index = variableList.get(fragments);
                    powers.set(index, powers.get(index) + 1);
                }
                else
                    try {
                        coefficient *= Double.parseDouble(fragments);
                    } catch(Exception e) {
                        throw new ExpressionCompileException("Can not resolve this expression");
                    }
            }
            if (Math.abs(coefficient) > numericError)
                compileResults.add(new NumericTerm(coefficient, powers));
        }
    }

    private void mergeResults() {
        boolean modified = false;
        do {
            modified = false;
            compileResults.sort(new CompareNumericTermByPowersHash());
            for(int i = 0; i < compileResults.size()-1; i++) {
                NumericTerm formerTerm = compileResults.get(i);
                NumericTerm latterTerm = compileResults.get(i + 1);
                if (formerTerm.powers.equals(latterTerm.powers)) {              // ������������ָ����ͬ, ѡ��ϲ�
                    double coefficientSum = formerTerm.coefficient + latterTerm.coefficient;
                    if (Math.abs(coefficientSum) < numericError) {              // ��ϵ����Ϊ0����ɾȥ���������һ�����ԭ��������
                        compileResults.remove(i + 1);
                        compileResults.remove(i);
                    } else {
                        compileResults.set(i, new NumericTerm(coefficientSum, latterTerm.powers));
                        compileResults.remove(i + 1);
                    }
                    modified = true;                                            // ��¼�˴εĺϲ�
                    break;                                                      // �������нṹ�ѱ��ƻ���Ӧ������һ�������ѭ��
                }
            }
        } while(modified == true);
    }

    private String transformNumericTermToString(NumericTerm term) {
        String result = Double.toString(term.coefficient);                      // ��ϵ��ת��Ϊ�ַ���
        for(int i = 0; i < variableNumber; i++) {
            int power = term.powers.get(i);                                     // ��λ�õĶ�Ӧָ��
            if (power > 0)
                result += "*" + variableIndexToName.get(i);                     // ָ����������ӱ�����
            if (power > 1)
                result += "^" + Integer.toString(power);                        // ָ������1������ݴ�
        }
        if (result.startsWith("1*"))
            return result.substring(2);                                         // ��Ϊ1*a��ʽ����ʡ��ǰ���1
        return result;
    }


}
