package edu.umn.crisys.observability.preprocessing.ast;

public enum TVL {
	True, 
	False, 
	Unknown;

	public TVL negate() {
		switch (this) {
			case True : return False;
			case False : return True;
			default : return Unknown;
		}
	}
}; 
