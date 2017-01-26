package org.usfirst.frc.team2046.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Interlock {
	
	private static final Interlock INSTANCE = new Interlock();
	private static final String OVERRIDE_INTERLOCKS = "Override Interlocks";
	private static final String INTERLOCKS_ACTIVATED = "Interlocks Activated";
	private boolean interlockOverridden = false;
	private boolean interlockActivated = false;
	
	/**
	 * Synchronized signleton - runs initialize on initial getInstance() case
	 */
	private Interlock() {
		initialize();
	}
	
	/**
	 * Gets the singleton instance of Interlock (this class).
	 * @return Returns the singleton instance of Interlock.
	 */
	public static Interlock getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Determines whether or not Interlock is overriden on the SmartDashboard.
	 * @return True = Interlock is overriden on the SmartDashboard, False = Interlock is not overridden on the SmartDashboard
	 */
	public boolean isOverriden() {
		return interlockOverridden;
	}
	
	/**
	 * Changes the Interlock activation state.
	 * @param interlockActivated true = Interlock is enabled, false = Interlock is disabled.
	 */
	public void setInterlockActivated(boolean interlockActivated) {
		this.interlockActivated  = interlockActivated;
	}
	
	/**
	 * SmartDashboard initialization for the Interlock value.
	 */
	public void initialize() {
		SmartDashboard.putBoolean(OVERRIDE_INTERLOCKS, false);	
	}
	
	/**
	 * Updates the SmartDashboard with the current values
	 */
	public void updateDashboard() {
		interlockOverridden = SmartDashboard.getBoolean(OVERRIDE_INTERLOCKS, false);
		SmartDashboard.putBoolean(INTERLOCKS_ACTIVATED, interlockActivated);
	}
}
