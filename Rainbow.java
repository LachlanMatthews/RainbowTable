import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Rainbow
{
    static class Password
    {
        String password;
        boolean used;

        Password(String pw, boolean u)
        {
            password = pw;
            used = u;
        }
    }

    public static void fillTable(ArrayList<Password[]> rT, String fileName) throws FileNotFoundException
    {
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);
        String pw = "";
        while (scanner.hasNextLine())
        {
            pw = scanner.nextLine();
            Password password = new Password(pw, false);
            rT.add(new Password[]{password, null});
        }
        int x = 0;
        while (rT.size() % 5 != 0)
        {
            Password password = new Password(pw, false);
            rT.add(new Password[]{password, null});
            x++;
        }
        System.out.println("Words read in: " + rT.size() + " (" + (rT.size() - x) + " + " + x + " copies of final word added)");
    }

    public static void writeTable(ArrayList<Password[]> rT) throws IOException
    {
        FileWriter file = new FileWriter("Rainbow.txt");
        for (Password[] passwords : rT)
        {
            //try
            {
                file.write(passwords[0].password + "  " + passwords[1].password + "\n");
            }
            //catch (Exception e)
            {

            }

        }
        file.close();
    }

    public static String MD5(ArrayList<Password[]> rT, int row, int column) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(rT.get(row)[column].password.getBytes());
        BigInteger bigInt = new BigInteger(1, messageDigest);
        String hash = bigInt.toString(16);
        while (hash.length() < 32)
        {
            hash = "0" + hash;
        }
        return hash;
    }

    public static void reduce(Password result, ArrayList<Password[]> rT, int i, int j)
    {
        try
        {
            result.password = rT.get(i + j + 1)[0].password;
            rT.get(i + j + 1)[0].used = true;
        }
        catch (Exception e)
        {
            System.out.println("Pre-image of hash does not exist in the table");
        }


    }

    public static void hashAndReduce(ArrayList<Password[]> rT) throws NoSuchAlgorithmException
    {
        for (int i = 0; i < rT.size(); i++)
        {
            if (!rT.get(i)[0].used)
            {
                String hash = MD5(rT, i, 0);
                Password result = new Password(hash, false);
                rT.get(i)[1] = result;
                rT.get(i)[0].used = true;

                for (int j = 0; j < 4; j++)
                {
                    reduce(result, rT, i, j);
                    rT.get(i)[1] = result;
                    rT.get(i)[1].used = true;
                    hash = MD5(rT, i, 1);
                    rT.get(i)[1].password = hash;
                    result.password = hash;
                }
            }
        }

        Iterator<Password[]> i = rT.iterator();
        while (i.hasNext())
        {
            Password[] p = i.next();
            if (p[1] == null)
            {
                i.remove();
            }
        }
    }

    public static ArrayList<Password[]> sortTable(ArrayList<Password[]> rT)
    {
        ArrayList<Password[]> sortedTable = new ArrayList<Password[]>();
        ArrayList<Character> chars = new ArrayList<Character>();
        String stringOfChars = "0123456789abcdefghijklmnopqrstuvwxyz";
        for (char c : stringOfChars.toCharArray())
        {
            chars.add(c);
        }

        return sortedTable;
    }

    public static void hashSearch(ArrayList<Password[]> rT, ArrayList<Password[]> w) throws NoSuchAlgorithmException
    {
        System.out.print("Enter hash (32 chars, containing only 0-9 a-f): ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().toLowerCase();
        int count = 0;
        while ((input.toCharArray().length != 32) || (count == 0))
        {
            char[] chars = new char[]{'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
            for (char c1 : input.toCharArray())
            {
                count = 0;
                for (char c2 : chars)
                {
                    if (c1 == c2)
                    {
                        count++;
                    }
                }
            }
            if (count == 0 || input.toCharArray().length != 32)
            {
                count = 0;
                System.out.print("Input must be 32 chars and 0-9 a-f: ");
                input = scanner.nextLine().toLowerCase();
            }
        }

        boolean match = false;
        System.out.println("Entered hash: " + input);
        System.out.println("-Searching for hash...");
        for (Password[] p : rT)
        {
            if (p[1].password.equals(input))
            {
                System.out.println("-Match found");
                System.out.println("-Hash chain start: " + p[0].password);
                System.out.println("-Reducing...");
                for (int i = 0; i < w.size(); i++)
                {
                    String hash = MD5(w, i, 0);
                    for (int j = 0; j < 4; j++)
                    {
                        if (hash.equals(input))
                        {
                            System.out.println("-Match found");
                            System.out.println("-Corresponding password: " + w.get(i + j)[0].password);
                            match = true;
                            break;
                        }
                        else
                        {
                            Password temp = new Password(hash, false);
                            reduce(temp, w, i, j);
                            MessageDigest md = MessageDigest.getInstance("MD5");
                            byte[] messageDigest = md.digest(temp.password.getBytes());
                            BigInteger bigInt = new BigInteger(1, messageDigest);
                            hash = bigInt.toString(16);
                            while (hash.length() < 32)
                            {
                                hash = "0" + hash;
                            }
                        }
                    }
                    if (match)
                    {
                        break;
                    }
                }
                match = true;
                break;
            }
        }
        if (!match)
        {
            System.out.println("-Match not found");
            System.out.println("-Reducing...");
            for (int i = 0; i < w.size(); i++)
            {
                String hash = MD5(w, i, 0);
                for (int j = 0; j < 4; j++)
                {
                    if (hash.equals(input))
                    {
                        System.out.println("-Match found");
                        System.out.println("-Corresponding password: " + w.get(i + j)[0].password);
                        match = true;
                        break;
                    }
                    else
                    {
                        Password temp = new Password(hash, false);
                        reduce(temp, w, i, j);
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        byte[] messageDigest = md.digest(temp.password.getBytes());
                        BigInteger bigInt = new BigInteger(1, messageDigest);
                        hash = bigInt.toString(16);
                        while (hash.length() < 32)
                        {
                            hash = "0" + hash;
                        }
                    }
                }
                if (match)
                {
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException
    {
      //Initialisation
        ArrayList<Password[]> rTable = new ArrayList<Password[]>();
        ArrayList<Password[]> words = new ArrayList<Password[]>();

      //Data manipulation
        fillTable(rTable, args[0]);
        fillTable(words, args[0]);
        hashAndReduce(rTable);
        System.out.println("Lines in rainbow table: " + rTable.size());
        //rTable = sortTable(rTable);
        writeTable(rTable);
        hashSearch(rTable, words);
    }
}