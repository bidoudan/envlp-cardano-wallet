package com.univocity.cardano.wallet.addresses;

import com.univocity.cardano.wallet.embedded.CardanoToolWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.univocity.cardano.wallet.addresses.AddressStyle.Shelley;


public class AddressBuilder extends CardanoToolWrapper {

    private static final Logger log = LoggerFactory.getLogger(AddressManager.class);
    private AddressStyle style = Shelley;

    public AddressBuilder(String toolDirectoryPath, String toolName) {
        super(toolDirectoryPath, toolName);
    }

    public String deriveKey(String publicRootKey, long sequentialDerivationIndex) {
        if (StringUtils.isBlank(publicRootKey)) {
            throw new IllegalArgumentException("Public root key cannot be null/blank");
        }
        if (sequentialDerivationIndex < 0) {
            throw new IllegalArgumentException("Sequential derivation index can't be negative. Got " + sequentialDerivationIndex);
        }
        return execute("derive key", publicRootKey, "key child 2/" + sequentialDerivationIndex, true);
    }



    public String generateSeed(int wordCount) {
        return executeNoInput("generate seed phrase", "recovery-phrase generate --size " + wordCount, false);
    }

    public String generateSeed() {
        return generateSeed(24);
    }

    public String generateRootPrivateKey(String seed) {
        if (seed == null) {
            seed = generateSeed();
        }
        if (seed != null) {
            String result = execute("generate private key", seed, "key from-recovery-phrase " + style.name(), false);
            if (result != null && result.toLowerCase().contains("error")) {
                log.warn("Error generating private key: " + result);
                return null;
            }
            return result;
        }
        return null;
    }

    public String generateHardenedPublicKeyPath(String privateKey) {
        if (StringUtils.isBlank(privateKey)) {
            throw new IllegalArgumentException("Private key cannot be null/blank");
        }
        String derivationPath = "1852H/1815H/0H";
        return execute("generate hardened public key path", privateKey, "key child " + derivationPath, false);
    }

    public String generateStakePublicKeyPath(String publicRootKey, long sequentialDerivationIndex) {
        if (StringUtils.isBlank(publicRootKey)) {
            throw new IllegalArgumentException("Public root key cannot be null/blank");
        }
        if (sequentialDerivationIndex < 0) {
            throw new IllegalArgumentException("Sequential derivation index can't be negative. Got " + sequentialDerivationIndex);
        }
        return execute("derive key", publicRootKey, "key child 2/" + sequentialDerivationIndex, true);
    }

    public String generateStakePublicKey(String publicRootKey,  long sequentialDerivationIndex) {
        return execute("", generateStakePublicKeyPath(publicRootKey, sequentialDerivationIndex), "key public --with-chain-code", false);
    }


    public String generatePaymentPublicKeyPath(String publicRootKey, long sequentialDerivationIndex) {
        if (StringUtils.isBlank(publicRootKey)) {
            throw new IllegalArgumentException("Public root key cannot be null/blank");
        }
        if (sequentialDerivationIndex < 0) {
            throw new IllegalArgumentException("Sequential derivation index can't be negative. Got " + sequentialDerivationIndex);
        }
        return execute("derive key", publicRootKey, "key child 0/" + sequentialDerivationIndex, true);
    }

    public String generatePaymentPublicKey(String publicRootKey,  long sequentialDerivationIndex) {
        return execute("", generatePaymentPublicKeyPath(publicRootKey, sequentialDerivationIndex), "key public --with-chain-code", false);
    }

    public String generatePaymentAddress(String publicRootKey, String network, long sequentialDerivationIndex) {
        return execute("", generatePaymentPublicKey(publicRootKey, sequentialDerivationIndex), "address payment --network-tag " + network, false);
    }

    public String generateDelegatedPaymentAddress(String publicRootKey, String network, long sequentialDerivationIndex) {
        return execute("", generatePaymentAddress(publicRootKey, network, sequentialDerivationIndex), "address delegation " + generateStakePublicKey(publicRootKey, 0), false);
    }


   /* public static void main(String[] args) {
        AddressBuilder addressBuilder = new AddressBuilder(System.getProperty("user.home") + "/Downloads/cardano-wallet-v2021-11-11-macos64/", "cardano-address");
        // String rootPrivateKey = addressBuilder.generateRootPrivateKey("exercise club noble adult miracle awkward problem olympic puppy private goddess piano fatal fashion vacuum");
        // String rootPrivateKey = addressBuilder.generateRootPrivateKey("nuclear unaware venture assist garment width body carpet tongue cabin quarter enforce reopen green clerk");
        String rootPrivateKey = addressBuilder.generateRootPrivateKey("fame print future fun share witness lonely typical knife glimpse solar circle good install world");
        String rootPublicKey = addressBuilder.generateHardenedPublicKeyPath(rootPrivateKey);

        System.out.println(addressBuilder.generateStakePublicKey(rootPublicKey, 0));
        System.out.println(addressBuilder.generateDelegatedPaymentAddress(rootPublicKey, "mainnet", 1));
    } */
}
