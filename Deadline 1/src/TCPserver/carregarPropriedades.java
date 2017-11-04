package TCPserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class carregarPropriedades extends Properties{

    public carregarPropriedades(String path){
        try{
            this.load(new InputStreamReader(new FileInputStream(path)));
        } catch (IOException e) {
            System.out.println("ficheiro de propriedades nao encontrado");
            System.exit(0);
        }
    }
}


