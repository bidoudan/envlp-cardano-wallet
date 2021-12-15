package com.univocity.cardano.wallet.exception;


public class CardanoToolsNotFoundException extends IllegalStateException{

	public CardanoToolsNotFoundException(String pathToCardanoToolsDir){
		super("Cardano tools directory not found: " + pathToCardanoToolsDir);
	}

}
