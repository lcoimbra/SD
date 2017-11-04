package RMIserver;

import TCPserver.carregarPropriedades;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class ligacaoBD {

    /*lê ficheiro de configuração da base de dados*/
    public static final carregarPropriedades pbd = new carregarPropriedades("iBei.properties");
    private final String BD_DRIVER = pbd.getProperty("driver");
    private final String BD_CONECAO = pbd.getProperty("connection");
    private final String BD_UTILIZADOR = pbd.getProperty("user");
    private final String BD_PWD = pbd.getProperty("pass");

    public ligacaoBD() {}

    synchronized public Connection getLigacaoBD() {
        Connection ligacao = null;
        try {
            Class.forName(BD_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Check databse driver!");
        }
        try {
            ligacao = DriverManager.getConnection(BD_CONECAO, BD_UTILIZADOR, BD_PWD);
            return ligacao;
        } catch (SQLException e) {
             /*System.out.println(e.getMessage());*/
        }
        return ligacao;
    }

    synchronized public int login(String utilizador, String pwd) throws SQLException {

        Connection ligacao = null;
        PreparedStatement preparedStatement = null;
        utilizador = utilizador.toLowerCase();


        String selectSQL = "SELECT utilizadorID FROM utilizador WHERE utilizador = ? AND password = ? AND banido = 0";
        try {
            ligacao = getLigacaoBD();
             preparedStatement = ligacao.prepareStatement(selectSQL);
            preparedStatement.setString(1, utilizador);
            preparedStatement.setString(2, pwd);

            /* execute select SQL stetement*/
            ResultSet rs = preparedStatement.executeQuery();
            // ver .next() para obter linha | se 0 linhas sai logo SENÃO tem 1 linha porque username é UNIQUE
            while (rs.next()) {
                return rs.getInt("utilizadorID");
            }
        } catch (SQLException e) {
            //System.out.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (ligacao != null) {
                ligacao.close();
            }
        }

        // Se resultado não tem rows
        return -1;
    }

    synchronized public int register(String utilizador, String pwd) throws SQLException {

        Connection ligacao = null;
        PreparedStatement preparedStatement = null, preparedStatement2 = null;
        utilizador = utilizador.toLowerCase();

        String selectSQL = "SELECT utilizadorID FROM utilizador WHERE utilizador = ?";
        // System.out.println("tentar login");
        try {
            ligacao = getLigacaoBD();
            //System.out.println(ligacao);
            preparedStatement = ligacao.prepareStatement(selectSQL);
            preparedStatement.setString(1, utilizador);

            /* execute select SQL stetement*/
            ResultSet rs = preparedStatement.executeQuery();

            // ver .next() para obter linha | se 0 linhas sai logo SENÃO tem 1 linha porque username é UNIQUE
            while(rs.next()) {
                return -1;
            }

            String selectSQL2 = "INSERT INTO utilizador (utilizadorid, utilizador, password, previlegios, banido) VALUES (s_utilizador.nextVal, ?, ?, 0, 0)";

            preparedStatement2 = ligacao.prepareStatement(selectSQL2);
            preparedStatement2.setString(1, utilizador);
            preparedStatement2.setString(2, pwd);

            preparedStatement2.executeQuery();

            return login(utilizador, pwd);
        } catch (SQLException e) {
            //e.printStackTrace();
            //System.out.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (preparedStatement2 != null) {
                preparedStatement2.close();
            }
            if (ligacao != null) {
                ligacao.close();
            }
        }
        // Se resultado não tem rows
        return -1;
    }

    synchronized public String createAuction(int utilizador, long codArtigo, String title, String description, String deadline, double amount) throws SQLException {

        Connection ligacao = null;
        PreparedStatement preparedStatement = null;

        String selectSQL = "INSERT INTO leilao (leilaoID, utilizadorID, artigo, titulo, descricao, preco_max, data_fim, estado) VALUES (s_leilao.nextVal, ?, ?, ?, ?, ?, TO_TIMESTAMP(?, 'RR-MM-DD HH24:MI:SS'), 0)";
        // System.out.println("tentar login");
        try {
            ligacao = getLigacaoBD();
            //System.out.println(ligacao);
            preparedStatement = ligacao.prepareStatement(selectSQL);
            preparedStatement.setInt(1, utilizador);
            preparedStatement.setLong(2, codArtigo);
            preparedStatement.setString(3, title);
            preparedStatement.setString(4, description);
            preparedStatement.setDouble(5, amount);
            preparedStatement.setString(6, deadline);

            /* execute select SQL stetement*/
            preparedStatement.executeQuery();

            return "type: create_auction, ok: true";

        } catch (SQLException e) {
            //System.out.println( e.getMessage());
            //e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (ligacao != null) {
                ligacao.close();
            }
        }

        // Se resultado não tem rows
        return "type: create_auction, ok: false";
    }

    synchronized public String searchAuction(long codArtigo) throws SQLException {

        Connection ligacao = null;
        PreparedStatement preparedStatement = null;
        String res = "", current;
        int i;

        String selectSQL = "SELECT leilaoID, titulo FROM leilao WHERE artigo = ?";

        // System.out.println("tentar login");
        try {
            ligacao = getLigacaoBD();
            //System.out.println(ligacao);
            preparedStatement = ligacao.prepareStatement(selectSQL);
            preparedStatement.setLong(1, codArtigo);

            /* execute select SQL stetement*/
            ResultSet rs = preparedStatement.executeQuery();

            i = 0;
            while (rs.next()) {
                current = "items_" + i;
                res = res + ", " + current + "_id: " + rs.getInt("leilaoID") + ", " + current + "_code: " + codArtigo + ", " + current + "_title: " + rs.getString("titulo");
                i++;
            }

            if (i > 0) {
                return "type: search_auction, items_count: " + i + res;
            }
        } catch (SQLException e) {
            //System.out.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (ligacao != null) {
                ligacao.close();
            }
        }
        // Se resultado não tem rows
        return "type: search_auction, items_count: 0";
    }

    synchronized public String detailAuction(int auctionID) throws SQLException {
        Connection ligacao = null;
        PreparedStatement preparedStatement = null, preparedStatement2 = null, preparedStatement3 = null, preparedStatement4 = null, preparedStatement5 = null;
        String res = "", msgs, current = "", bids, versions;
        int i, checkAuction, j;

        checkAuction = isAuctionEndedCanceled(auctionID);

        if (checkAuction == -2)
            return "type: detail_auction, ok: false";

        String selectSQL = "SELECT * FROM leilao WHERE leilaoID = ?";

        try {
            ligacao = getLigacaoBD();
            //System.out.println(ligacao);
            preparedStatement = ligacao.prepareStatement(selectSQL);
            preparedStatement.setInt(1, auctionID);

            /* execute select SQL stetement*/
            ResultSet rs = preparedStatement.executeQuery();
            // ver .next() para obter linha | se 0 linhas sai logo SENÃO tem 1 linha porque username é UNIQUE
            if (rs.next()) {
                // get messages
                String selectSQL2 = "SELECT utilizador.utilizador, mensagem FROM msg_mural, utilizador WHERE leilaoID = ? AND utilizador.utilizadorID = msg_mural.utilizadorID";
                preparedStatement2 = ligacao.prepareStatement(selectSQL2);
                preparedStatement2.setInt(1, auctionID);

                /* execute select SQL stetement*/
                ResultSet rs2 = preparedStatement2.executeQuery();

                i = 0;
                msgs = "";
                while (rs2.next()) {
                    current = "messages_" + i;
                    msgs = msgs + current + "_user: " + rs2.getString("utilizador") + ", " + current + "_text: " + rs2.getString("mensagem") + ", ";
                    i++;
                }

                res = "type: detail_auction, title: " + rs.getString("titulo") + ", description: " + rs.getString("descricao") + ", deadline: " + rs.getTimestamp("data_fim").toString() + ", messages_count: " + i + ", ";

                if (i > 0)
                    res = res + msgs;

                // get bids
                String selectSQL3 = "SELECT utilizador.UTILIZADOR, valor FROM licitacao, utilizador WHERE leilaoID = ? AND utilizador.utilizadorID = licitacao.utilizadorID ORDER BY valor DESC";
                preparedStatement3 = ligacao.prepareStatement(selectSQL3);
                preparedStatement3.setInt(1, auctionID);

                /* execute select SQL stetement*/
                ResultSet rs3 = preparedStatement3.executeQuery();
                i = 0;
                bids = "";
                while (rs3.next()) {
                    current = "bids_" + i;
                    bids = bids + ", " + current + "_user: " + rs3.getString("utilizador") + ", " + current + "_amount: " + rs3.getDouble("valor");
                    i++;
                }

                // final bids
                if (i > 0)
                    res = res + "bids_count: " + i + bids;
                else
                    res = res + "bids_count: 0";

                // get old versions
                String selectSQL4 = "SELECT registoID FROM registo WHERE leilaoID = ?";
                preparedStatement4 = ligacao.prepareStatement(selectSQL4);
                preparedStatement4.setInt(1, auctionID);

                /* execute select SQL stetement*/
                ResultSet rs4 = preparedStatement4.executeQuery();
                j = 0;
                versions = "";
                while (rs4.next()) {
                    current = "version_" + j + "_id: ";
                    versions = versions + ", " + current + rs4.getInt("registoID");
                    j++;
                }

                res = res + ", old_versions_count: " + j;

                // final old version
                if (j > 0) {
                    res = res + versions;
                }

                if (checkAuction == 0)
                    return res + ", status: running";
                else if (checkAuction == 1)
                    return res + ", status: ended";
                else if (checkAuction == -1)
                    return res + ", status: cancelled";
            } else {
                System.out.println("entrei!!!");
                String selectSQL5 = "SELECT * FROM registo WHERE registoID = ?";
                preparedStatement5 = ligacao.prepareStatement(selectSQL5);
                preparedStatement5.setInt(1, auctionID);

                /* execute select SQL stetement*/
                ResultSet rs5 = preparedStatement5.executeQuery();
                rs5.next();
                return "type: detail_auction, title: " + rs5.getString("titulo") + ", description: " + rs5.getString("descricao") + ", code: " + rs5.getLong("artigo") + ", deadline: " + rs5.getTimestamp("data_fim").toString() + ", amount: " + rs5.getDouble("preco_max") + ", status: old";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (preparedStatement2 != null) {
                preparedStatement2.close();
            }
            if (preparedStatement3 != null) {
                preparedStatement3.close();
            }
            if (preparedStatement4 != null) {
                preparedStatement4.close();
            }
            if (preparedStatement5 != null) {
                preparedStatement5.close();
            }
            if (ligacao != null) {
                ligacao.close();
            }
        }

        // Se resultado não tem rows
        return "type: detail_auction, ok: false";
    }

    synchronized public String myAuctions(int userID) throws SQLException {

        Connection ligacao = null;
        PreparedStatement preparedStatement = null, preparedStatement2 = null, preparedStatement3 = null;
        String res = "", current;
        int i;
        ArrayList<Integer> codigos = new ArrayList<>();

        String selectSQL = "SELECT leilaoID, artigo, titulo FROM leilao WHERE utilizadorID = ?";

        try {
            ligacao = getLigacaoBD();
            //System.out.println(ligacao);
            preparedStatement = ligacao.prepareStatement(selectSQL);
            preparedStatement.setInt(1, userID);

            /* execute select SQL stetement*/
            ResultSet rs = preparedStatement.executeQuery();
            // ver .next() para obter linha | se 0 linhas sai logo SENÃO tem 1 linha porque username é UNIQUE
            i = 0;
            while (rs.next()) {
                if (!codigos.contains(rs.getInt("leilaoID"))) {
                    codigos.add(rs.getInt("leilaoID"));
                    current = "items_" + i;
                    res = res + ", " + current + "_id: " + rs.getInt("leilaoID") + ", " + current + "_code: " + rs.getLong("artigo") + ", " + current + "_title: " + rs.getString("titulo");
                    i++;
                }
            }

            String selectSQL2 = "SELECT leilao.leilaoID, artigo, titulo, licitacao.leilaoID FROM leilao, licitacao WHERE licitacao.utilizadorID = ? AND leilao.leilaoID = licitacao.leilaoID";

            preparedStatement2 = ligacao.prepareStatement(selectSQL2);
            preparedStatement2.setInt(1, userID);

            ResultSet rs2 = preparedStatement2.executeQuery();

            while (rs2.next()) {
                if (!codigos.contains(rs2.getInt("leilaoID"))) {
                    codigos.add(rs2.getInt("leilaoID"));
                    current = "items_" + i;
                    res = res + ", " + current + "_id: " + rs2.getInt("leilaoID") + ", " + current + "_code: " + rs2.getLong("artigo") + ", " + current + "_title: " + rs2.getString("titulo");
                    i++;
                }
            }

            String selectSQL3 = "SELECT leilao.leilaoID, artigo, titulo, msg_mural.leilaoID FROM leilao, msg_mural WHERE msg_mural.utilizadorID = ? AND leilao.leilaoID = msg_mural.leilaoID";

            preparedStatement3 = ligacao.prepareStatement(selectSQL3);
            preparedStatement3.setInt(1, userID);

            ResultSet rs3 = preparedStatement3.executeQuery();

            while (rs3.next()) {
                if (!codigos.contains(rs3.getInt("leilaoID"))) {
                    codigos.add(rs3.getInt("leilaoID"));
                    current = "items_" + i;
                    res = res + ", " + current + "_id: " + rs3.getInt("leilaoID") + ", " + current + "_code: " + rs3.getLong("artigo") + ", " + current + "_title: " + rs3.getString("titulo");
                    i++;
                }
            }
            //System.out.println("i: "+i);
            if (i > 0)
                return "type: my_auctions, items_count: " + i + res;

        } catch (SQLException e) {
           // System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (preparedStatement2 != null) {
                preparedStatement2.close();
            }
            if (preparedStatement3 != null) {
                preparedStatement3.close();
            }
            if (ligacao != null) {
                ligacao.close();
            }
        }

        // Se resultado não tem rows
        return "type: my_auctions, items_count: 0";
    }

    synchronized public ArrayList<Integer> bid (int utilizador, int auctionID, double amount) throws SQLException {

        Connection ligacao = null;
        PreparedStatement preparedStatement = null, preparedStatement2 = null, preparedStatement3 = null, preparedStatement4 = null;
        double min;
        int checkAuction;
        ArrayList<Integer> notificacoes = new ArrayList<>();

        checkAuction = isAuctionEndedCanceled(auctionID);

        if (amount <= 0)
            return null;
        if (checkAuction != 0) {
            return null;
        }

        String selectSQL = "SELECT NVL(MIN(valor), 99999.99) as minimo FROM licitacao WHERE licitacao.leilaoID = ?";

        try {
            ligacao = getLigacaoBD();
            //System.out.println(ligacao);
            preparedStatement = ligacao.prepareStatement(selectSQL);
            preparedStatement.setInt(1, auctionID);

            /* execute select SQL stetement*/
            ResultSet rs = preparedStatement.executeQuery();

            min = Double.MAX_VALUE;
            if (rs.next()) {
                min = rs.getDouble("minimo");
            }

            if (min > amount) {
                String selectSQL2 = "SELECT preco_max FROM leilao WHERE leilaoID = ? AND utilizadorID <> ?";

                preparedStatement2 = ligacao.prepareStatement(selectSQL2);
                preparedStatement2.setInt(1, auctionID);
                preparedStatement2.setInt(2, utilizador);

                ResultSet rs2 = preparedStatement2.executeQuery();

                if (rs2.next()) {
                    if (rs2.getDouble("preco_max") > amount) {
                        String selectSQL3 = "INSERT INTO licitacao (licitacaoID, utilizadorID, leilaoID, valor) VALUES (s_licitacao.nextVal, ?, ?, ?)";
                        preparedStatement3 = ligacao.prepareStatement(selectSQL3);
                        preparedStatement3.setInt(1, utilizador);
                        preparedStatement3.setInt(2, auctionID);
                        preparedStatement3.setDouble(3, amount);

                        preparedStatement3.executeQuery();

                        String selectSQL4 = "SELECT utilizadorID FROM licitacao WHERE leilaoID = ?";
                        preparedStatement4 = ligacao.prepareStatement(selectSQL4);
                        preparedStatement4.setInt(1, auctionID);

                        ResultSet rs4 = preparedStatement4.executeQuery();

                        while (rs4.next()) {
                            if (!notificacoes.contains(rs4.getInt("utilizadorID")))
                                notificacoes.add(rs4.getInt("utilizadorID"));
                        }

                        return notificacoes;
                    }
                }
            }
        } catch (SQLException e) {
            //System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (preparedStatement2 != null) {
                preparedStatement2.close();
            }
            if (preparedStatement3 != null) {
                preparedStatement3.close();
            }
            if (preparedStatement4 != null) {
                preparedStatement4.close();
            }
            if (ligacao != null) {
                ligacao.close();
            }
        }

        return null;
    }

    synchronized public String editAuction (int utilizador, int auctionID, long codArtigo, String title, String description, String deadline, double amount) throws SQLException {

        Connection ligacao = null;
        PreparedStatement preparedStatement = null, preparedStatement2 = null, preparedStatement0 = null;
        int i, checkAuction;
        boolean previous = false;

        checkAuction = isAuctionEndedCanceled(auctionID);

        if (checkAuction != 0)
            return "type: edit_auction, ok: false";

        String selectSQL = "SELECT * FROM leilao WHERE leilaoID = ? AND utilizadorID = ?";

        try {
            ligacao = getLigacaoBD();
            //System.out.println(ligacao);
            preparedStatement = ligacao.prepareStatement(selectSQL);
            preparedStatement.setInt(1, auctionID);
            preparedStatement.setInt(2, utilizador);

            /* execute select SQL stetement*/
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                String selectSQL0 = "INSERT INTO registo (registoID, leilaoID, titulo, descricao, preco_max, data_fim, artigo, estado) VALUES (s_leilao.nextval, ?, ?, ?, ?, ?, ?, ?)";
                preparedStatement0 = ligacao.prepareStatement(selectSQL0);
                preparedStatement0.setInt(1, auctionID);
                preparedStatement0.setString(2, rs.getString("titulo"));
                preparedStatement0.setString(3, rs.getString("descricao"));
                preparedStatement0.setDouble(4, rs.getDouble("preco_max"));
                preparedStatement0.setTimestamp(5, rs.getTimestamp("data_fim"));
                preparedStatement0.setLong(6, rs.getLong("artigo"));
                preparedStatement0.setInt(7, rs.getInt("estado"));

                preparedStatement0.executeQuery();

                String selectSQL2 = "UPDATE leilao SET";

                if (codArtigo != -1) {
                    selectSQL2 = selectSQL2 + " artigo = ?";
                    previous = true;
                }
                if (title != null) {
                    if (previous)
                        selectSQL2 = selectSQL2 + ",";
                    selectSQL2 = selectSQL2 + " titulo = ?";
                    previous = true;
                }
                if (description != null) {
                    if (previous)
                        selectSQL2 = selectSQL2 + ",";
                    selectSQL2 = selectSQL2 + " descricao = ?";
                    previous = true;
                }
                if (deadline != null) {
                    if (previous)
                        selectSQL2 = selectSQL2 + ",";
                    selectSQL2 = selectSQL2 + " data_fim = ?";
                    previous = true;
                }
                if (amount != -1) {
                    if (previous)
                        selectSQL2 = selectSQL2 + ",";
                    selectSQL2 = selectSQL2 + " preco_max = ?";
                }

                selectSQL2 = selectSQL2 + " WHERE leilaoID = ?";
                preparedStatement2 = ligacao.prepareStatement(selectSQL2);
                i = 1;

                if (codArtigo != -1) {
                    preparedStatement2.setLong(i, codArtigo);
                    i++;
                }
                if (title != null) {
                    preparedStatement2.setString(i, title);
                    i++;
                }
                if (description != null) {
                    preparedStatement2.setString(i, description);
                    i++;
                }
                if (deadline != null) {
                    preparedStatement2.setString(i, deadline);
                    i++;
                }
                if (amount != -1) {
                    preparedStatement2.setDouble(i, amount);
                    i++;
                }

                preparedStatement2.setDouble(i, auctionID);

                preparedStatement2.executeQuery();
                return "type: edit_auction, ok: true";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (preparedStatement2 != null) {
                preparedStatement2.close();
            }
            if (preparedStatement0 != null) {
                preparedStatement0.close();
            }
            if (ligacao != null) {
                ligacao.close();
            }
        }

        // Se resultado não tem rows
        return "type: edit_auction, ok: false";
    }

    synchronized public Map message (int utilizador, int auctionID, String text) throws SQLException {

        Connection ligacao = null;
        PreparedStatement preparedStatement = null, preparedStatement2 = null;
        Map<Integer, String> m = new HashMap<>();
        int checkAuction;

        checkAuction = isAuctionEndedCanceled(auctionID);

        if (checkAuction != 0)
            return null;

        String selectSQL = "SELECT s_msg_mural.nextval as numero FROM dual";

        try {
            ligacao = getLigacaoBD();
            //System.out.println(ligacao);
            preparedStatement = ligacao.prepareStatement(selectSQL);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                String selectSQL2 = "INSERT INTO msg_mural (msgID, utilizadorID, leilaoID, mensagem) VALUES (?, ?, ?, ?)";
                preparedStatement2 = ligacao.prepareStatement(selectSQL2);
                preparedStatement2.setInt(1, rs.getInt("numero"));
                preparedStatement2.setInt(2, utilizador);
                preparedStatement2.setInt(3, auctionID);
                preparedStatement2.setString(4, text);

                preparedStatement2.executeQuery();

                m.put(rs.getInt("numero"), text);

                return m;
            }
        } catch (SQLException e) {
           //System.out.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (preparedStatement2 != null) {
                preparedStatement2.close();
            }
            if (ligacao != null) {
                ligacao.close();
            }
        }
        return null;
    }

    synchronized public Map notificationMessage_offline (int utilizador) throws SQLException {

        Connection ligacao = null;
        PreparedStatement preparedStatement = null;
        Map<Integer, String> m = new HashMap<>();
        int i;

        try {

            ligacao = getLigacaoBD();
            //System.out.println(ligacao);

            String selectSQL = "SELECT msg_notificacao.msgID as IDmsg, leilaoID, utilizador, mensagem FROM msg_notificacao, msg_mural, utilizador WHERE msg_notificacao.utilizadorID = ? AND entregue = 0 AND msg_notificacao.msgID = msg_mural.msgID AND msg_mural.utilizadorID = utilizador.utilizadorID";

            preparedStatement = ligacao.prepareStatement(selectSQL);
            preparedStatement.setInt(1, utilizador);

            /* execute select SQL stetement*/
            ResultSet rs = preparedStatement.executeQuery();

            i = 0;
            while (rs.next()) {
                m.put(rs.getInt("IDmsg"), "type: notification_message, id: " + rs.getInt("leilaoID") + ", user: " + rs.getString("utilizador") + ", text: " + rs.getString("mensagem"));
                i++;
            }

            if (i > 0) {
                return m;
            }

        } catch (SQLException e) {
           // System.out.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (ligacao != null) {
                ligacao.close();
            }
        }
        // Se resultado não tem rows
        return null;
    }

    synchronized public String cancelAuction (int utilizador, int auctionID) throws SQLException {
        Connection ligacao = null;
        PreparedStatement preparedStatement = null, preparedStatement2 = null;
        int check;

        try {

            ligacao = getLigacaoBD();
            //System.out.println(ligacao);

            check = checkAdmin(utilizador);
            if (check == 0 || check != 1) { // == 0 utilizador nao existe, != 1 nao tem previlegios admin
                return "type: cancel_auction, ok: false";
            }
            String selectSQL = "SELECT leilaoID FROM leilao WHERE leilaoID = ?";

            preparedStatement = ligacao.prepareStatement(selectSQL);
            preparedStatement.setInt(1, auctionID);

            /* execute select SQL stetement*/
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                //System.out.println("update");
                String selectSQL2 = "UPDATE leilao SET estado = -1 WHERE leilaoID = ?";

                preparedStatement2 = ligacao.prepareStatement(selectSQL2);
                preparedStatement2.setInt(1, auctionID);

                /* execute select SQL stetement*/
                preparedStatement2.executeQuery();

                return "type: cancel_auction, ok: true";
            }

        } catch (SQLException e) {
           // System.out.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (preparedStatement2 != null) {
                preparedStatement2.close();
            }
            if (ligacao != null) {
                ligacao.close();
            }
        }
        // Se resultado não tem rows
        return "type: cancel_auction, ok: false";
    }

    synchronized public ArrayList<Integer> banUser (int utilizador, String ban) throws SQLException {
        Connection ligacao = null;
        PreparedStatement preparedStatement = null, preparedStatement2 = null, preparedStatement3 = null, preparedStatement4 = null, preparedStatement5 = null, preparedStatement6 = null;
        int check, idOld, registerOld;
        boolean elimina, novo;
        double valor;
        ArrayList<Integer> criarMensagens = new ArrayList<>();

        try {
            ligacao = getLigacaoBD();
            //System.out.println(ligacao);

            check = checkAdmin(utilizador);
            if (check == 0 || check != 1) { // == 0 utilizador nao existe, != 1 nao tem previlegios admin
                return null;
            }

            String selectSQL = "SELECT utilizadorID, banido FROM utilizador WHERE utilizador = ?";

            preparedStatement = ligacao.prepareStatement(selectSQL);
            preparedStatement.setString(1, ban);

            /* execute select SQL stetement*/
            ResultSet rs = preparedStatement.executeQuery();
            //System.out.println("cenas");
            if (rs.next()) {
                if (rs.getInt("banido") == 1) {
                    criarMensagens.add(-1);
                    return criarMensagens;
                }

                // Cancelar leiloes do utilizador banido
                String selectSQL2 = "UPDATE leilao SET estado = -1 WHERE utilizadorID = ? AND data_fim > SYSDATE";

                preparedStatement2 = ligacao.prepareStatement(selectSQL2);
                preparedStatement2.setInt(1, rs.getInt("utilizadorID"));

                /* execute select SQL stetement*/
                preparedStatement2.executeQuery();

                // Apagar licitacoes do utilizador banido e fazer operacoes necessarias
                String selectSQL3 = "SELECT leilaoID FROM licitacao WHERE utilizadorID = ? GROUP BY leilaoID";

                preparedStatement3 = ligacao.prepareStatement(selectSQL3);
                preparedStatement3.setInt(1, rs.getInt("utilizadorID"));

                /* execute select SQL stetement*/
                ResultSet rs3 = preparedStatement3.executeQuery();
                //System.out.println("cenas2");
                while (rs3.next()) {
                    String selectSQL4 = "SELECT licitacaoID, utilizadorID, valor FROM licitacao WHERE leilaoID = ? ORDER BY valor DESC";

                    preparedStatement4 = ligacao.prepareStatement(selectSQL4);
                    preparedStatement4.setInt(1, rs3.getInt("leilaoID"));

                    /* execute select SQL stetement*/
                    ResultSet rs4 = preparedStatement4.executeQuery();

                    elimina = false;
                    novo = false;
                    valor = -1;
                    registerOld = 0;
                    idOld = 0;
                    //System.out.println("cenas3");
                    while (rs4.next()) {
                        if (elimina) {
                            if (!novo) {
                                String selectSQL5 = "DELETE FROM licitacao WHERE licitacaoID = ?";

                                preparedStatement5 = ligacao.prepareStatement(selectSQL5);
                                preparedStatement5.setInt(1, registerOld);

                                /* execute select SQL stetement*/
                                preparedStatement5.executeQuery();

                                preparedStatement5.close();
                            } else {
                                if (idOld != 0) {
                                    String selectSQL5 = "DELETE FROM licitacao WHERE licitacaoID = ?";

                                    preparedStatement5 = ligacao.prepareStatement(selectSQL5);
                                    preparedStatement5.setInt(1, idOld);

                                    /* execute select SQL stetement*/
                                    preparedStatement5.executeQuery();

                                    preparedStatement5.close();
                                }

                                idOld = registerOld;
                            }
                        }

                        novo = false;
                        if (rs4.getInt("utilizadorID") == rs.getInt("utilizadorID") && valor == -1) {
                            valor = rs4.getDouble("valor");
                            elimina = true;
                        } else if (rs4.getInt("utilizadorID") != rs.getInt("utilizadorID")) {
                            novo = true;
                        }

                        registerOld = rs4.getInt("licitacaoID");
                    }
                    //System.out.println("cenas4");
                    if (elimina) {
                        if (!novo) {
                            String selectSQL5 = "DELETE FROM licitacao WHERE licitacaoID = ?";

                            preparedStatement5 = ligacao.prepareStatement(selectSQL5);
                            preparedStatement5.setInt(1, registerOld);

                            /* execute select SQL stetement*/
                            preparedStatement5.executeQuery();

                            preparedStatement5.close();
                        } else {
                            String selectSQL5 = "DELETE FROM licitacao WHERE licitacaoID = ?";

                            preparedStatement5 = ligacao.prepareStatement(selectSQL5);
                            preparedStatement5.setInt(1, idOld);

                            /* execute select SQL stetement*/
                            preparedStatement5.executeQuery();

                            preparedStatement5.close();

                            idOld = registerOld;
                        }

                        String selectSQL5 = "UPDATE licitacao SET valor = ? WHERE licitacaoID = ?";

                        preparedStatement5 = ligacao.prepareStatement(selectSQL5);
                        preparedStatement5.setDouble(1, valor);
                        preparedStatement5.setInt(2, idOld);

                        /* execute select SQL stetement*/
                        preparedStatement5.executeQuery();

                        preparedStatement5.close();

                        criarMensagens.add(rs3.getInt("leilaoID"));
                    }
                    //System.out.println("fim");
                }

                String selectSQL6 = "UPDATE utilizador SET banido = 1 WHERE utilizador = ?";

                preparedStatement6 = ligacao.prepareStatement(selectSQL6);
                preparedStatement6.setString(1, ban);

                /* execute select SQL stetement*/
                preparedStatement6.executeQuery();

                if (criarMensagens.size() != 0) {
                    return criarMensagens;
                } else {
                     criarMensagens.add(-1);
                    return criarMensagens;
                }
            }

        } catch (SQLException e) {
           // System.out.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (preparedStatement2 != null) {
                preparedStatement2.close();
            }
            if (preparedStatement3 != null) {
                preparedStatement3.close();
            }
            if (preparedStatement4 != null) {
                preparedStatement4.close();
            }
            if (preparedStatement5 != null) {
                preparedStatement5.close();
            }
            if (preparedStatement6 != null) {
                preparedStatement6.close();
            }
            if (ligacao != null) {
                ligacao.close();
            }
        }
        // Se resultado não tem rows
        return null;
    }

    synchronized public String stats (int utilizador) throws SQLException {
        Connection ligacao = null;
        PreparedStatement preparedStatement = null, preparedStatement2 = null, preparedStatement3 = null, preparedStatement4 = null, preparedStatement5 = null;
        int check, i;
        String res;

        try {

            ligacao = getLigacaoBD();
            //System.out.println(ligacao);

            check = checkAdmin(utilizador);
            if (check == 0 || check != 1) { // == 0 utilizador nao existe, != 1 nao tem previlegios admin
                return "type: stats, ok: false";
            }

            String selectSQL = "SELECT utilizadorID, COUNT(leilaoID) as contagem FROM leilao GROUP BY utilizadorID ORDER BY contagem DESC";

            preparedStatement = ligacao.prepareStatement(selectSQL);

            /* execute select SQL stetement*/
            ResultSet rs = preparedStatement.executeQuery();

            i = 0;
            res = "";
            while (rs.next() && i < 10) {
                String selectSQL2 = "SELECT utilizador FROM utilizador WHERE utilizadorID = ?";

                preparedStatement2 = ligacao.prepareStatement(selectSQL2);
                preparedStatement2.setInt(1, rs.getInt("utilizadorID"));
                /* execute select SQL stetement*/
                ResultSet rs2 = preparedStatement2.executeQuery();

                if (rs2.next()) {
                    res = res + ", top10_auction_creation_" + i + ": " + rs2.getString("utilizador");
                    i++;
                }

                preparedStatement2.close();
            }

            String selectSQL3 = "SELECT NVL(vencedorid,0) as vence, COUNT(leilaoID) as contagem FROM leilao GROUP BY vencedorid ORDER BY contagem DESC";

            preparedStatement3 = ligacao.prepareStatement(selectSQL3);

            /* execute select SQL stetement*/
            ResultSet rs3 = preparedStatement3.executeQuery();

            i = 0;
            while (rs3.next() && i < 10) {
                String selectSQL4 = "SELECT utilizador FROM utilizador WHERE utilizadorID = ?";

                preparedStatement4 = ligacao.prepareStatement(selectSQL4);
                preparedStatement4.setInt(1, rs3.getInt("vence"));
                /* execute select SQL stetement*/
                ResultSet rs4 = preparedStatement4.executeQuery();

                if (rs4.next()) {
                    res = res + ", top10_auction_won_" + i + ": " + rs4.getString("utilizador");
                    i++;
                }

                preparedStatement4.close();
            }

            String selectSQL5 = "SELECT COUNT(leilaoID) as contagem FROM leilao WHERE data_fim BETWEEN sysdate - 10 AND sysdate";

            preparedStatement5 = ligacao.prepareStatement(selectSQL5);

            /* execute select SQL stetement*/
            ResultSet rs5 = preparedStatement5.executeQuery();

            if (rs5.next())
                res = res + ", auction_closed_last_10_days: " + rs5.getInt("contagem");
            else
                res = res + ", auction_closed_last_10_days: 0";

            return "type: stats" + res;
        } catch (SQLException e) {
            //System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (preparedStatement2 != null) {
                preparedStatement2.close();
            }
            if (preparedStatement3 != null) {
                preparedStatement3.close();
            }
            if (preparedStatement4 != null) {
                preparedStatement4.close();
            }
            if (preparedStatement5 != null) {
                preparedStatement5.close();
            }
            if (ligacao != null) {
                ligacao.close();
            }
        }
        // Se resultado não tem rows
        return "type: stats, ok: false";
    }

    synchronized public void removeFromMsgNotifcacao (int utilizador, int msgID) throws SQLException {
        Connection ligacao = null;
        PreparedStatement preparedStatement = null, preparedStatement2 = null;

        try {

            ligacao = getLigacaoBD();
            //System.out.println(ligacao);

            String selectSQL = "SELECT entregue FROM msg_notificacao WHERE utilizadorID = ? AND msgID = ? AND entregue <> 1";

            preparedStatement = ligacao.prepareStatement(selectSQL);
            preparedStatement.setInt(1, utilizador);
            preparedStatement.setInt(2, msgID);

            /* execute select SQL stetement*/
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String selectSQL2 = "UPDATE msg_notificacao SET entregue = 1 WHERE utilizadorID = ? AND msgID = ?";

                preparedStatement2 = ligacao.prepareStatement(selectSQL2);
                preparedStatement2.setInt(1, utilizador);
                preparedStatement2.setInt(2, msgID);

                /* execute select SQL stetement*/
                preparedStatement2.executeQuery();
            }

        } catch (SQLException e) {
            //System.out.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (preparedStatement2 != null) {
                preparedStatement2.close();
            }
            if (ligacao != null) {
                ligacao.close();
            }
        }
    }

    synchronized public int checkAdmin (int utilizador) throws SQLException {
        Connection ligacao = null;
        PreparedStatement preparedStatement = null;

        try {
            ligacao = getLigacaoBD();
            //System.out.println(ligacao);

            String selectSQL = "SELECT previlegios FROM utilizador WHERE utilizadorID = ?";

            preparedStatement = ligacao.prepareStatement(selectSQL);
            preparedStatement.setInt(1, utilizador);

            /* execute select SQL stetement*/
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                return rs.getInt("previlegios");
            }


        } catch (SQLException e) {
            //System.out.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (ligacao != null) {
                ligacao.close();
            }
        }

        // Se resultado não tem rows
        return 0;
    }

    synchronized public ArrayList<Integer> createNotifications (int msgID) throws SQLException {
        Connection ligacao = null;
        PreparedStatement preparedStatement = null, preparedStatement2 = null, preparedStatement3 = null, preparedStatement4 = null;
        ArrayList<Integer> notificacoes = new ArrayList<>();

        try {

            ligacao = getLigacaoBD();
            //System.out.println(ligacao);

            String selectSQL = "SELECT msg_mural.leilaoID as leilaoID, msg_mural.utilizadorID as msgUser, leilao.utilizadorID as leilaoUser FROM msg_mural, leilao WHERE msgID = ? AND leilao.leilaoID = msg_mural.leilaoID";
            preparedStatement = ligacao.prepareStatement(selectSQL);
            preparedStatement.setInt(1, msgID);

            /* execute select SQL stetement*/
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                if (rs.getInt("leilaoUser") != rs.getInt("msgUser"))
                    notificacoes.add(rs.getInt("leilaoUser"));

                String selectSQL2 = "SELECT utilizadorID FROM licitacao WHERE leilaoID = ?";

                preparedStatement2 = ligacao.prepareStatement(selectSQL2);
                preparedStatement2.setInt(1, rs.getInt("leilaoID"));

                /* execute select SQL stetement*/
                ResultSet rs2 = preparedStatement2.executeQuery();

                while (rs2.next()) {
                    if (rs2.getInt("utilizadorID") != rs.getInt("msgUser") && !notificacoes.contains(rs2.getInt("utilizadorID"))) {
                        notificacoes.add(rs2.getInt("utilizadorID"));
                    }
                }

                String selectSQL3 = "SELECT utilizadorID FROM msg_mural WHERE leilaoID = ?";

                preparedStatement3 = ligacao.prepareStatement(selectSQL3);
                preparedStatement3.setInt(1, rs.getInt("leilaoID"));

                /* execute select SQL stetement*/
                ResultSet rs3 = preparedStatement3.executeQuery();

                while (rs3.next()) {
                    if (rs3.getInt("utilizadorID") != rs.getInt("msgUser") && !notificacoes.contains(rs3.getInt("utilizadorID"))) {
                        notificacoes.add(rs3.getInt("utilizadorID"));
                    }
                }

                for (int i = 0; i < notificacoes.size(); i++) {
                    String selectSQL4 = "INSERT INTO msg_notificacao VALUES (?, ?, 0)";

                    preparedStatement4 = ligacao.prepareStatement(selectSQL4);
                    preparedStatement4.setInt(1, notificacoes.get(i));
                    preparedStatement4.setInt(2, msgID);

                /* execute select SQL stetement*/
                    preparedStatement4.executeQuery();

                    preparedStatement4.close();
                }

                return notificacoes;
            }

        } catch (SQLException e) {
            //System.out.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (preparedStatement2 != null) {
                preparedStatement2.close();
            }
            if (preparedStatement3 != null) {
                preparedStatement3.close();
            }
            if (preparedStatement4 != null) {
                preparedStatement4.close();
            }
            if (ligacao != null) {
                ligacao.close();
            }
        }
        return null;
    }

    synchronized public int isAuctionEndedCanceled (int leilaoID) throws SQLException {
        Connection ligacao = null;
        PreparedStatement preparedStatement = null, preparedStatement2 = null, preparedStatement3 = null, preparedStatement4 = null;

        try {

            ligacao = getLigacaoBD();
            //System.out.println(ligacao);

            String selectSQL = "SELECT estado, data_fim FROM leilao WHERE leilaoID = ?";

            preparedStatement = ligacao.prepareStatement(selectSQL);
            preparedStatement.setInt(1, leilaoID);

            /* execute select SQL stetement*/
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {

                if (rs.getInt("estado") != 0) {
                    return rs.getInt("estado");
                }

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String dataAtual = dateFormat.format(date); //2014/08/06 15:59:48
                //Timestamp timestampAtual = new Timestamp(0);
                Timestamp timestampAtual;
                timestampAtual = Timestamp.valueOf(dataAtual);

                Timestamp dataFim = rs.getTimestamp("data_fim");

                if (timestampAtual.compareTo(dataFim) < 0) {
                    return 0;
                }

                String selectSQL3 = "SELECT valor, utilizadorID FROM licitacao WHERE leilaoID = ? ORDER BY valor DESC";

                preparedStatement3 = ligacao.prepareStatement(selectSQL3);
                preparedStatement3.setInt(1, leilaoID);

                /* execute select SQL stetement*/
                ResultSet rs3 = preparedStatement3.executeQuery();

                //System.out.println("vai passar");
                int utilizadorWin = 0;
                //double valorWin;
                while (rs3.next()) {
                    utilizadorWin = rs3.getInt("utilizadorID");
                    //System.out.println("entrei");
                    //valorWin = rs3.getDouble("valor");
                }
                //System.out.printf("passou");
                String selectSQL2;
                if (utilizadorWin == 0) {
                    selectSQL2 = "UPDATE leilao SET estado = 1 WHERE leilaoID = ?";
                    preparedStatement2 = ligacao.prepareStatement(selectSQL2);
                    preparedStatement2.setInt(1, leilaoID);
                } else {
                    selectSQL2 = "UPDATE leilao SET estado = 1, vencedorid = ? WHERE leilaoID = ?";
                    preparedStatement2 = ligacao.prepareStatement(selectSQL2);
                    preparedStatement2.setInt(1, utilizadorWin);
                    preparedStatement2.setInt(2, leilaoID);
                }

                /* execute select SQL stetement*/
                preparedStatement2.executeQuery();

                return 1;
            } else {
                System.out.println("entrei is cenas");
                String selectSQL2 = "SELECT registoID FROM registo WHERE registoID = ?";

                preparedStatement4 = ligacao.prepareStatement(selectSQL2);
                preparedStatement4.setInt(1, leilaoID);

            /* execute select SQL stetement*/
                ResultSet rs4 = preparedStatement4.executeQuery();

                if (rs4.next()) {
                    return 1;
                }
            }

        } catch (SQLException e) {
           System.out.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (preparedStatement2 != null) {
                preparedStatement2.close();
            }
            if (preparedStatement3 != null) {
                preparedStatement3.close();
            }
            if (ligacao != null) {
                ligacao.close();
            }
        }

        // nao existe leilao
        return -2;
    }

    synchronized public void removeAuction (int leilaoID) throws SQLException {
        Connection ligacao = null;
        PreparedStatement preparedStatement = null, preparedStatement2 = null, preparedStatement3 = null;

        try {

            ligacao = getLigacaoBD();
            //System.out.println(ligacao);

            String selectSQL = "SELECT leilaoID FROM leilao WHERE leilaoID = ?";

            preparedStatement = ligacao.prepareStatement(selectSQL);
            preparedStatement.setInt(1, leilaoID);

            /* execute select SQL stetement*/
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                String selectSQL2 = "DELETE FROM licitacao WHERE leilaoID = ?";

                preparedStatement2 = ligacao.prepareStatement(selectSQL2);
                preparedStatement2.setInt(1, rs.getInt("leilaoID"));

                /* execute select SQL stetement*/
                preparedStatement2.executeQuery();

                String selectSQL3 = "DELETE FROM leilao WHERE leilaoID = ?";

                preparedStatement3 = ligacao.prepareStatement(selectSQL3);
                preparedStatement3.setInt(1, rs.getInt("leilaoID"));

                /* execute select SQL stetement*/
                preparedStatement3.executeQuery();
            }


        } catch (SQLException e) {
            //System.out.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (preparedStatement2 != null) {
                preparedStatement2.close();
            }
            if (preparedStatement3 != null) {
                preparedStatement3.close();
            }
            if (ligacao != null) {
                ligacao.close();
            }
        }
    }
}