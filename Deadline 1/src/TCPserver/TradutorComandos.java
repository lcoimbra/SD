package TCPserver;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by joao on 18/10/2016.
 */
public class TradutorComandos {
    String type;
    Map m = new HashMap<>();
    boolean erro;

    public TradutorComandos(String str){
        this.erro=false;
        try {
            String cleanString = str.replaceAll("\r", "").replaceAll("\n", "");
            str=cleanString;
            if(!str.contains(",")){
                StringTokenizer st = new StringTokenizer(str,":");
                if (st.nextToken().contains("type")) {
                    this.type = st.nextToken().replaceAll("\\s+", "");
                    //System.out.println(this.type);
                    this.m.put("type", this.type);
                }
            }else {
                StringTokenizer st = new StringTokenizer(str,",");
                while (st.hasMoreTokens()) {
                    StringTokenizer token = new StringTokenizer(st.nextToken(), ":");
                    String aux = token.nextToken();
                    if (aux.contains("type")) {
                        this.type = token.nextToken().trim();
                        this.m.put("type", this.type);
                    } else {
                        String s = aux.trim();
                        String ss = (token.nextToken().trim());
                       /* StringBuilder sb = new StringBuilder(ss);*/
                        this.m.put(s, ss);
                    }
                }
            }
        } catch(Exception e) {
            //e.printStackTrace();
            this.erro=true;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map getM() {
        return m;
    }

    public void setM(Map m) {
        this.m = m;
    }

    public boolean isErro() {
        return erro;
    }

    public void setErro(boolean erro) {
        this.erro = erro;
    }

}
