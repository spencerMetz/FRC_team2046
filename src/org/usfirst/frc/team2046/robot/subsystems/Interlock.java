package org.usfirst.frc.team2046.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Interlock {

	private static final Interlock INSTANCE = new Interlock();
	
	private static final String OVERRIDE_INTERLOCKS = "Override Interlocks";

	private static final String INTERLOCKS_ACTIVATED = "Interlocks Activated";

	private boolean interlockOverridden = false;

	private boolean interlockActivated = false;
	
	private Interlock() {
		initialize();
	}
	
	public static Interlock getInstance() {
		return INSTANCE;
	}
	
	public boolean isOverriden() {
		return interlockOverridden;
	}
	
	public void setInterlockActivated(boolean interlockActivated) {
		this.interlockActivated  = interlockActivated;
	}
	
	public void initialize() {
		SmartDashboard.putBoolean(OVERRIDE_INTERLOCKS, false);	
	}
		
	public void updateDashboard() {
		interlockOverridden = SmartDashboard.getBoolean(OVERRIDE_INTERLOCKS, false);
		SmartDashboard.putBoolean(INTERLOCKS_ACTIVATED, interlockActivated);
	}
}
