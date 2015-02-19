package com.goreacraft.plugins.goreaparty;

import java.lang.invoke.WrongMethodTypeException;


@SuppressWarnings("serial")
public class CommandException extends WrongMethodTypeException {
    /**
	 * goreacraft
	 */
	//private static final long serialVersionUID = 1L;

	public CommandException(String key, Object... args) {
        super("Something went wrong here");
    }
}
