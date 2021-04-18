import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Stats {
    static String fileName = "_chat.txt";
    public static void main(String[] args) {
        Scanner scanner = null;
        int ok=1;
        if(!(new File(fileName).exists())){
            FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
            dialog.setMode(FileDialog.LOAD);
            dialog.setVisible(true);
            String file = dialog.getFile();
            fileName=file;
        }
        while(ok==1){
            System.out.println("1. Show number of messages per day for a certain person");
            System.out.println("2. Count all messages");
            System.out.print("Option: ");
            scanner = new Scanner(System.in);
            String optiune = scanner.nextLine();
            switch(Integer.parseInt(optiune)){
                case 1:{
                    System.out.print("Name of the person: ");
                    afisare_data_mesaje(scanner.nextLine());
                    break;
                }
                case 2:{
                    afisare_numar_mesaje();
                    break;
                }
                case 0:{
                    ok=0;
                    break;
                }
            }
            System.out.println("Press Enter key to continue");
            scanner.nextLine();
        }
    }
    public static void afisare_data_mesaje(String persoana){
        System.out.println(persoana);
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(fileName));
            TreeMap<Date,Integer> fmes = new TreeMap<>();
            while(scanner.hasNext()){
                String linie = scanner.nextLine();
                linie=linie.replace(Character.toString('\u200E'),"");
                String [] cuvinte = procesareLinie(linie);
                if(cuvinte != null){
                    if(cuvinte.length==3 && (cuvinte[1].equals(persoana) || persoana.equals("tot"))) {
                        try {
                            Date data = new SimpleDateFormat("dd.MM.yyyy").parse(cuvinte[0].split(" ", 2)[0]);
                            if (fmes.containsKey(data)) {
                                fmes.put(data, fmes.get(data) + 1);
                            } else {
                                fmes.put(data, 1);
                            }
                        }catch(ParseException pe){
                            // :(
                        }
                    }
                }
            }
            int suma = 0;
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream("output.csv"), StandardCharsets.UTF_8);
            outputStreamWriter.write("Data,Total Mesaje,Numar mesaje zi,"+persoana+"\n");
            //outputStreamWriter.write("Data,Total Mesaje,Numar mesaje zi,"+persoana+"\n");
            for(Map.Entry<Date, Integer>  aux: fmes.entrySet() )
            {
                suma=suma+aux.getValue();
                outputStreamWriter.write((new SimpleDateFormat("dd.MM.yyyy").format(aux.getKey())+"," + suma+","+aux.getValue()+"\n"));
            }
            outputStreamWriter.flush();
            outputStreamWriter.close();
            Desktop dt = Desktop.getDesktop();
            dt.open(new File("output.csv"));
        } catch(FileNotFoundException ex){
            ex.printStackTrace();
        } catch(IOException ex){
            ex.printStackTrace();
        }

    }
    public static void afisare_numar_mesaje(){
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(fileName));
            TreeMap<String,Integer> fmes = new TreeMap<>();
            while(scanner.hasNext()){
                String linie = scanner.nextLine();
                String [] cuvinte = procesareLinie(linie);
                if(cuvinte != null){
                    if(fmes.containsKey(cuvinte[1])){
                        fmes.put(cuvinte[1],fmes.get(cuvinte[1])+1);
                    } else{
                        fmes.put(cuvinte[1],1);
                    }
                }
            }
            int suma=0;
            for(Map.Entry<String, Integer>  aux: fmes.entrySet() )
            {
                System.out.println(aux.getKey()+" -> " + aux.getValue());
                suma=suma+aux.getValue();
            }
            System.out.println("Total: "+suma);
        } catch(FileNotFoundException ex){
            ex.printStackTrace();
        }
    }

    public static String[] procesareLinie(String linie) {
        try {
            if(linie.charAt(0)=='[') {
                String[] cuvinte = new String[3];
                String[] aux = linie.split(" ", 5);
                if (aux.length == 5) {
                    aux[0] = aux[0].replace("[", "");
                    aux[0] = aux[0].replace(",", "");
                    aux[2] = aux[2].replace("]", "");
                    cuvinte[0] = aux[0] + " " + aux[1] + " " + aux[2];
                    cuvinte[1] = aux[3].replace(":", "");
                    cuvinte[2] = aux[4];
                    return cuvinte;
                }
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
            System.out.println("EROARE: " + linie);
        } catch (StringIndexOutOfBoundsException ex){
            // :(
        }
        return null;
    }
}
