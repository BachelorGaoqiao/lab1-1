/**
 * Created by zxx_1 on 2016/9/18.
 */
public class Term {                                 // ���������ŵ�����ʽ
    public String content;
    public int sign;                              // '\0'��ʾ��,'\1'��ʾ��
    Term(String content,char sign) {
        this.content = content;
        this.sign = sign;
    }
    Term() {
        this.content = "";
        this.sign = '\0';
    }
}
