package com.stibits.cardano.wallet.addresses;

import com.stibits.cardano.wallet.common.Utils;
import com.stibits.cardano.wallet.embedded.CardanoToolWrapper;
import org.apache.commons.lang3.*;
import org.slf4j.*;

public class AddressManager extends CardanoToolWrapper {

	private static final Logger log = LoggerFactory.getLogger(AddressManager.class);
	private AddressStyle style = AddressStyle.Shelley;

	public AddressManager(String cardanoToolsDirPath) {
		super(cardanoToolsDirPath, "cardano-address");
	}

	public AddressStyle getStyle() {
		return style;
	}

	public AddressManager setStyle(AddressStyle style) {
		Utils.notNull(style, "Address style");
		AddressManager out = new AddressManager(this.toolDir.getAbsolutePath());
		out.style = style;
		return out;
	}

	public String generateSeed(int wordCount) {
		return executeNoInput("generate seed phrase", "recovery-phrase generate --size " + wordCount, false);
	}

	public String generateSeed() {
		return generateSeed(24);
	}


	public String generatePublicKeyFromSeed(String seed) {
		return generatePublicKey(generateRootPrivateKey(seed));
	}

	public String generatePublicKey(String privateKey) {
		if (StringUtils.isBlank(privateKey)) {
			throw new IllegalArgumentException("Private key cannot be null/blank");
		}
		return execute("generate public key", privateKey, "key public --with-chain-code --", false);
	}

	public String generateHardenedPublicKeyPathFromSeed(String seed, long accountIndex) {
		return generateHardenedPublicKeyPath(generateRootPrivateKey(seed), accountIndex);
	}

	public String generateHardenedPublicKeyPath(String privateKey, long accountIndex) {
		if (StringUtils.isBlank(privateKey)) {
			throw new IllegalArgumentException("Private key cannot be null/blank");
		}
		if (accountIndex < 0) {
			throw new IllegalArgumentException("Account can't be negative. Got " + accountIndex);
		}

		String derivationPath = "1852H/1815H/";
		return execute("generate hardened public key path", privateKey, "key child " + derivationPath + accountIndex + "H", false);
	}

	public String generatePublicRootKeyFromPrivateKey(String privateKey, long accountIndex) {
		String hardenedPublicKeyPath = generateHardenedPublicKeyPath(privateKey, accountIndex);
		return execute("generate public root key", hardenedPublicKeyPath, "key public --with-chain-code", true);
	}

	public String generatePublicRootKeyFromSeed(String seed, long accountIndex) {
		if (StringUtils.isBlank(seed)) {
			throw new IllegalArgumentException("Seed phrase cannot be null/blank");
		}
		seed = seed.trim();
		if (style == AddressStyle.Shelley && seed.split(" ").length != 24) {
			throw new IllegalArgumentException("Seed phrase must have 24 words");
		}
		return generatePublicRootKeyFromPrivateKey(generateRootPrivateKey(seed), accountIndex);
	}

	public String deriveKey(String publicRootKey, long sequentialDerivationIndex) {
		if (StringUtils.isBlank(publicRootKey)) {
			throw new IllegalArgumentException("Public root key cannot be null/blank");
		}
		if (sequentialDerivationIndex < 0) {
			throw new IllegalArgumentException("Sequential derivation index can't be negative. Got " + sequentialDerivationIndex);
		}
		return execute("derive key", publicRootKey, "key child 0/" + sequentialDerivationIndex, true);
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


	public String generatePaymentVerificationKey(String rootPrivateKey) {
		String paymentVerificationDerivation = execute("Generate payment verification key", rootPrivateKey, "key child 1852H/1815H/0H/0/0", false);
		return execute("Generate payment verification key", paymentVerificationDerivation, "key public --with-chain-code", false);
	}

	public String generatePaymentAddress(String paymentVerificationKey, String network, int i) {
		paymentVerificationKey = deriveKey(paymentVerificationKey, i);
		return execute("", paymentVerificationKey, "address payment --network-tag " + network, false);
	}

	public String generateStakeFromRootPrivateKey(String rootPrivateKey) {

		String staking = execute("generate a stake verification key", rootPrivateKey, "key child 1852H/1815H/0H/2/0", false);
		return execute("generate a stake verification key", staking, "key public --with-chain-code", true);
	}
	public String generateDelegationAddress(String stakeKey, String paymentAddress, int i) {

		return execute("Generate a delegated payment address from a stake key", paymentAddress, "address delegation " + stakeKey, false);
	}

	public String generatePaymentAddress(String derivedKey, int isMainNet) {
		if (StringUtils.isBlank(derivedKey)) {
			throw new IllegalArgumentException("Derived key must not be null/blank");
		}
		return execute("generate payment address", derivedKey, "address delegation --network-tag " + isMainNet, false);
	}

	public String generatePaymentAddressFromPublicRootKey(String publicRootKey, int isMainNet, long sequentialDerivationIndex) {
		String derivedKey = deriveKey(publicRootKey, sequentialDerivationIndex);
		return generatePaymentAddress(derivedKey, isMainNet);
	}





	public static void main(String[] args) {
		AddressManager addressManager = new AddressManager(System.getProperty("user.home") + "/Downloads/cardano-wallet-v2021-11-11-macos64/");
		/*for(int i = 0; i < 10; i++) {
			String pubRootKey = addressManager.generatePublicRootKeyFromSeed("inspire frozen amateur length error drink pilot flush joy portion reunion achieve harbor coyote memory around clump million comfort enough fantasy attitude drill purse", 0);
			String addr = addressManager.generatePaymentAddressFromPublicRootKey(pubRootKey, 1, i);

		}*/
		//String paymentAddress = addressManager.generatePaymentAddress();
		//String rootPrivateKey = addressManager.generateRootPrivateKey("nuclear unaware venture assist garment width body carpet tongue cabin quarter enforce reopen green clerk");
		String rootPrivateKey = addressManager.generateRootPrivateKey("pyramid weather edit gown must olympic proof jump match chase always abuse box prison sausage matter switch tattoo total tail voice present length column");
		//String rootPrivateKey = addressManager.generateRootPrivateKey("tongue behind name mix quick galaxy essence treat large danger narrow outdoor speed steel average");
		String paymentVerificationKey = addressManager.generatePaymentVerificationKey(rootPrivateKey);
		System.out.println(paymentVerificationKey);
		String stake = addressManager.generateStakeFromRootPrivateKey(rootPrivateKey);
		String paymentAddress = addressManager.generatePaymentAddress(paymentVerificationKey, "mainnet", 0);
		System.out.println("payment address " + paymentAddress);
		String delegationAddress = addressManager.generateDelegationAddress(stake, paymentAddress, 0);
		System.out.println(delegationAddress);

	}
}
