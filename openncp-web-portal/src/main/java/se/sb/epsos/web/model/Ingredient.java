/***    Copyright 2011-2013 Apotekens Service AB <epsos@apotekensservice.se>
*
*    This file is part of epSOS-WEB.
*
*    epSOS-WEB is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
*
*    epSOS-WEB is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License along with epSOS-WEB. If not, see http://www.gnu.org/licenses/.
**/package se.sb.epsos.web.model;

import java.io.Serializable;

public class Ingredient implements Serializable {
	private static final long serialVersionUID = 5984829943046464308L;

	private String activeIngredient;

	private String activeIngredientName;

	private String strength;

	public String getActiveIngredient() {
		return activeIngredient;
	}

	public void setActiveIngredient(String activeIngredient) {
		this.activeIngredient = activeIngredient;
	}

	public String getActiveIngredientName() {
		return activeIngredientName;
	}

	public void setActiveIngredientName(String activeIngredientName) {
		this.activeIngredientName = activeIngredientName;
	}

	public String getStrength() {
		return strength;
	}

	public void setStrength(String strength) {
		this.strength = strength;
	}

}
