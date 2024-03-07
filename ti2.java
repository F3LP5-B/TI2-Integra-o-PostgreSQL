import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Produto {
    private int id;
    private String nome;
    private double preco;

    // Construtores, getters e setters
    public Produto() {}

    public Produto(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    // getters e setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    // toString para facilitar a exibição dos dados
    @Override
    public String toString() {
        return "Produto [id=" + id + ", nome=" + nome + ", preco=" + preco + "]";
    }
}

public class ProdutoDAO {
    private Connection conexao;
    private String url = "jdbc:postgresql://localhost:5432/seu_banco_de_dados";
    private String usuario = "seu_usuario";
    private String senha = "sua_senha";

    // Construtor para conectar ao banco de dados
    public ProdutoDAO() throws SQLException {
        conexao = DriverManager.getConnection(url, usuario, senha);
    }

    // Método para inserir um produto no banco de dados
    public void inserir(Produto produto) throws SQLException {
        String sql = "INSERT INTO Produto (nome, preco) VALUES (?, ?)";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, produto.getNome());
        stmt.setDouble(2, produto.getPreco());
        stmt.executeUpdate();
        stmt.close();
    }

    // Método para listar todos os produtos do banco de dados
    public List<Produto> listar() throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM Produto";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Produto produto = new Produto();
            produto.setId(rs.getInt("id"));
            produto.setNome(rs.getString("nome"));
            produto.setPreco(rs.getDouble("preco"));
            produtos.add(produto);
        }
        rs.close();
        stmt.close();
        return produtos;
    }

    // Método para excluir um produto do banco de dados
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM Produto WHERE id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
    }

    // Método para atualizar um produto no banco de dados
    public void atualizar(Produto produto) throws SQLException {
        String sql = "UPDATE Produto SET nome = ?, preco = ? WHERE id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, produto.getNome());
        stmt.setDouble(2, produto.getPreco());
        stmt.setInt(3, produto.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    // Método para fechar a conexão com o banco de dados
    public void fechar() throws SQLException {
        conexao.close();
    }
}

public class Principal {
    private static ProdutoDAO produtoDAO;

    public static void main(String[] args) {
        try {
            produtoDAO = new ProdutoDAO();
            Scanner scanner = new Scanner(System.in);
            int opcao;
            do {
                System.out.println("\nMenu:");
                System.out.println("1. Listar produtos");
                System.out.println("2. Inserir produto");
                System.out.println("3. Excluir produto");
                System.out.println("4. Atualizar produto");
                System.out.println("5. Sair");
                System.out.print("Escolha uma opção: ");
                opcao = scanner.nextInt();
                switch (opcao) {
                    case 1:
                        listarProdutos();
                        break;
                    case 2:
                        inserirProduto();
                        break;
                    case 3:
                        excluirProduto();
                        break;
                    case 4:
                        atualizarProduto();
                        break;
                    case 5:
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválida!");
                }
            } while (opcao != 5);
            produtoDAO.fechar(); // Fechar conexão com o banco de dados ao sair
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void listarProdutos() throws SQLException {
        List<Produto> produtos = produtoDAO.listar();
        if (produtos.isEmpty()) {
            System.out.println("Não há produtos cadastrados.");
        } else {
            System.out.println("Lista de produtos:");
            for (Produto produto : produtos) {
                System.out.println(produto);
            }
        }
    }

    private static void inserirProduto() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nome do produto: ");
        String nome = scanner.nextLine();
        System.out.print("Preço do produto: ");
        double preco = scanner.nextDouble();
        Produto produto = new Produto(nome, preco);
        produtoDAO.inserir(produto);
        System.out.println("Produto inserido com sucesso.");
    }

    private static void excluirProduto() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("ID do produto a ser excluído: ");
        int id = scanner.nextInt();
        produtoDAO.excluir(id);
        System.out.println("Produto excluído com sucesso.");
    }

    private static void atualizarProduto() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("ID do produto a ser atualizado: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Limpar o buffer do scanner
        System.out.print("Novo nome do produto: ");
        String nome = scanner.nextLine();
        System.out.print("Novo preço do produto: ");
        double preco = scanner.nextDouble();
        Produto produto = new Produto(nome, preco);
        produto.setId(id);
        produtoDAO.atualizar(produto);
        System.out.println("Produto atualizado com sucesso.");
    }
}
