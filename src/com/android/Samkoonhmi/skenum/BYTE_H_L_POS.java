package com.android.Samkoonhmi.skenum;

/**
 * define high byte at low byte first or low byte at high byte first.
 * as we concerned about just is: at 32 bit ,low 16 bit at first or high 16 bit at first. 
 * @author Latory
 *
 */
public enum BYTE_H_L_POS {
	L_BYTE_FIRST,                   // low byte at first of high byte.
	H_BYTE_FIRST,                   // high byte at first of low byte.
	OTHER_H_L_POS                   // other high or low pos 
}
