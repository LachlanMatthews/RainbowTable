Compile: $ javac Rainbow.java

Run: $ java Rainbow Passwords.txt

Rainbow.txt contains the generated rainbow table. 1000 words from Passwords.txt are now stored in only 200 words with help from the reduction function.

The user inputs an MD5 hash and the program reduces it to determine if there is a match in the rainbow table. This program is meant to represent a hacker using stolen password hashes against a dictionary of common passwords stored in a rainbow table. The hash is computed against the rainbow table and determiens if there is a match (this represents that the attacker has determined the plaintext of the stolen hash). The reduction function allows more words to be stored in the hash of the plaintext to decrease search time.

Reduction function connects the hash of one possible password to the next password in the list of words read in. If x % 5 != 0 then I duplicate the final password read in so that the chain can complete the 5 loops of hashing/reducing without the final current hash being affected.

MD5 implementation: https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/security/MessageDigest.html
