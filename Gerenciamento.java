import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Gerenciamento {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        ArrayList<Livro> livros = new ArrayList<>();
        int escolha;
        do{
            System.out.println("Selecione uma das opções: ");
            System.out.println("1 - Adicionar Livros");
            System.out.println("2 - Listar livros");
            System.out.println("3 - Excluir livros");
            System.out.println("4 - Resevar/devolver livros");
            System.out.println("5 - Sair");
            escolha = sc.nextInt();

            if(escolha < 1 || escolha > 5){
                System.out.println("Opção inválida.");
                continue;
            }

            switch(escolha){
                case 1:
                    System.out.println("Adicionar livros:");
                    Livro livro = adicionarLivro(sc);
                    if (livro != null){
                        livros.add(livro);
                        System.out.println("Livro adicionado com sucesso!");

                        String nomeArquivo = livro.getTitulo().replace(" ","_") + ".txt";
                        new Gerenciamento().salvarDados(nomeArquivo, "biblioteca", livro);
                    }
                    break;
                case 2:
                    System.out.println("Listar Livros:");
                    listarLivros("biblioteca");
                    break;
                case 3:
                    System.out.println("Excluir livros:");
                    excluirLivros("biblioteca", sc);
                    break;
                case 4:
                    System.out.println("Reservar/Devolver livros:");
                    reservarLivros("biblioteca", sc);
                    break;
                case 5:
                    System.out.println("Saindo do programa.");
                    break;
                default:
                    System.out.println("Opção não encontrada. Por favor escolha as opções indicadas.");         
            }
        }while(escolha != 5);
        sc.close();
    } 

    public final void criarPasta(String pasta){
        Path diretorio = Paths.get(pasta);
        try{
            Files.createDirectory(diretorio);
            System.out.println("Arquivo criado!");
        } catch(IOException e) {
            System.out.println("Error ao criar arquivo: "+ e.getMessage());
        }
    }

    public final void salvarDados(String dados, String pasta, Livro livro){
        Path diretorio = Paths.get(pasta,dados);
        try{
            if (Files.notExists(diretorio.getParent())){
                Files.createDirectories(diretorio.getParent());
            }
            if (Files.notExists(diretorio)){
                Files.createFile(diretorio);
            }
            String conteudo = String.format("Titulo: %s\nAutor: %S\nAno: %d\nReservado: %s\n", livro.getTitulo(), livro.getAutor(), livro.getAno(), livro.isReserva()? "Sim":"Não");
            Files.writeString(diretorio, conteudo, StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Dados do livro salvos no arquivo: "+ diretorio.getFileName());
        } catch (IOException e) {
            System.out.println("Error ao criar arquivo: "+ e.getMessage());
        }
    }

    public static Livro adicionarLivro(Scanner sc){
        Livro livro = new Livro();

        System.out.println("Qual é o nome do livro?");
        sc.nextLine();
        String titulo = sc.nextLine();
        if (titulo.isBlank()){
            System.out.println("Nenhum titulo fornecido!");
            return null;
        }else{
            livro.setTitulo(titulo);
        }

        System.out.println("Qual o nome do autor?");
        String autor = sc.nextLine();
        if (autor.isBlank()){
            System.out.println("Nenhum autor fornecido!");
            return null;
        }else{
            livro.setAutor(autor);
        }

        System.out.println("O ano do livro?");
        try{
            int ano = sc.nextInt();
            livro.setAno(ano);
        } catch (Exception e) {
            System.out.println("Nenhum ano válido foi fornecido!");
            return null;
        } 
        livro.setReserva(false);
        return livro;
    }
    public static void listarLivros(String pasta){
        Path caminho = Paths.get(pasta);

        if (!Files.exists(caminho)){ 
            System.out.println("A pasta '"+ pasta +"' não existe.");
            return;
        }

        System.out.println("Listando livros na pasta: "+pasta);
        try{
            Files.list(caminho).forEach(arquivo -> {
                if (Files.isRegularFile(arquivo)){
                    System.out.println("\nConteúdo do arquivo: "+arquivo.getFileName());
                    try{
                        String conteudo = Files.readString(arquivo);
                        System.out.println(conteudo);
                    }catch (IOException e){
                        System.out.println("Erro ao ler arquivo: "+ arquivo.getFileName());
                    }
                }
            });
        } catch(IOException e){
            System.out.println("Erro ao listar livros: " + e.getMessage());
        }
    }
    public static void excluirLivros(String pasta, Scanner sc){
        Path caminho = Paths.get(pasta);

        if(!Files.exists(caminho)){
            System.out.println("A pasta '" + pasta +"' não existe.");
            return;
        }
            listarLivros(pasta);
            sc.nextLine();
        
            System.out.println("Qual livro deseja excluir? ");
            String lixo = sc.nextLine();
            try{
                Files.list(caminho).forEach(arquivo ->{
                    if(Files.isRegularFile(arquivo)){
                        System.out.println("\n conteúdo do arquivo: " + arquivo.getFileName());
                        if(lixo.equals(arquivo.getFileName().toString())){
                            try{
                                Files.delete(arquivo);
                                System.out.println("Arquivo excluído com sucesso: "+arquivo);
                            }catch(IOException e){
                                System.out.println(arquivo + "não foi encontrado");
                            }
                        }
                    }
                });
            } catch(IOException e){
                System.out.println("Error ao excluir o arquivo: "+ e.getMessage());
            }
    }
    public static void reservarLivros(String pasta, Scanner sc){
        Path caminho = Paths.get(pasta);
        if(!Files.exists(caminho)){
            System.out.println("A pasta '"+ pasta +"' não existe.");
            return;
        }
            listarLivros(pasta);
            sc.nextLine();

            System.out.println("Qual livro deseja reservar/devolver?");
            String tituloArquivo = sc.nextLine().replace("", "_") + ".txt";
            try {
                Path arquivoLivro = caminho.resolve(tituloArquivo);
                if (Files.exists(arquivoLivro)){
                    String conteudo = Files.readString(arquivoLivro);
                    boolean reservado = conteudo.contains("Reservado: Sim");
                    
                    String novoConteudo;
                    if(reservado){
                        novoConteudo = conteudo.replace("Reservado: Sim", "Reservado: Não");
                        System.out.println("A reserva do livro foi cancelada.");
                    }else{
                        novoConteudo = conteudo.replace("Reservado: Não", "Reservado: Sim");
                        System.out.println("O livro foi reservado com sucesso");
                    }
                    Files.writeString(arquivoLivro, novoConteudo, StandardOpenOption.TRUNCATE_EXISTING);
                }else{
                    System.out.println("O livro não foi encontrado");
                }
            } catch (Exception e) {
                System.out.println("Erro ao acessar o arquivo: "+ e.getMessage());
            }
    }
}
class Livro{
    private String titulo;
    private String autor;
    private int ano;
    private boolean reserva;

    public String getTitulo(){
        return titulo;
    }

    public void setTitulo(String titulo){
        this.titulo = titulo;
    }

    public String getAutor(){
        return autor;
    }

    public void setAutor(String autor){
        this.autor = autor;
    }

    public int getAno(){
        return ano;
    }

    public void setAno(int ano){
        if (ano > 0){
        this.ano = ano;
        } else {
            System.out.println("Ano inválido! Digite um valor positivo");
        }
    }

    public void setReserva(boolean reserva){
        this.reserva = reserva;
    }

    public boolean isReserva(){
        return reserva;
    }

}