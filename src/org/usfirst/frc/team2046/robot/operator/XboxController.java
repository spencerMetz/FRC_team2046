package org.usfirst.frc.team2046.robot.operator;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.RumbleType;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class XboxController {
	public static final int LEFT_LATERAL_AXIS = 0;
	public static final int LEFT_LONGITUDIAL_AXIS = 1;
	public static final int LEFT_TRIGGER_AXIS = 2;
	public static final int RIGHT_TRIGGER_AXIS = 3;
	public static final int RIGHT_LATERAL_AXIS = 4;
	public static final int RIGHT_LONGITUDIAL_AXIS = 5;
	public static final int DPAD_WEST_EAST_AXIS = 6;

	public static final int BUTTON_A = 1;
	public static final int BUTTON_B = 2;
	public static final int BUTTON_X = 3;
	public static final int BUTTON_Y = 4;
	public static final int BUTTON_LEFT_BUMPER = 5;
	public static final int BUTTON_RIGHT_BUMPER = 6;
	public static final int BUTTON_BACK = 7;
	public static final int BUTTON_START = 8;
	public static final int BUTTON_LEFT_STICK = 9;
	public static final int BUTTON_RIGHT_STICK = 10;

	private static final double DEAD_BAND = 9.0 / 127.0;

	protected final Joystick joystick;

	public final JoystickButton buttonA;
	public final JoystickButton buttonB;
	public final JoystickButton buttonX;
	public final JoystickButton buttonY;
	public final JoystickButton leftBumper;
	public final JoystickButton rightBumper;
	public final JoystickButton buttonBack;
	public final JoystickButton buttonStart;
	public final JoystickButton buttonLeftStick;
	public final JoystickButton buttonRightStick;

	public XboxController(int port) {
		joystick = new Joystick(port);

		buttonA = new JoystickButton(joystick, BUTTON_A);
		buttonB = new JoystickButton(joystick, BUTTON_B);
		buttonX = new JoystickButton(joystick, BUTTON_X);
		buttonY = new JoystickButton(joystick, BUTTON_Y);
		leftBumper = new JoystickButton(joystick, BUTTON_LEFT_BUMPER);
		rightBumper = new JoystickButton(joystick, BUTTON_RIGHT_BUMPER);
		buttonBack = new JoystickButton(joystick, BUTTON_BACK);
		buttonStart = new JoystickButton(joystick, BUTTON_START);
		buttonLeftStick = new JoystickButton(joystick, BUTTON_LEFT_STICK);
		buttonRightStick = new JoystickButton(joystick, BUTTON_RIGHT_STICK);
	}

	private double removeOffset(double value) {
		return Math.abs(value) > DEAD_BAND ? value : 0.0;
	}

	public void setRightRumble(float intensity) {
		joystick.setRumble(RumbleType.kRightRumble, intensity);
	}

	public void setLeftRumble(float intensity) {
		joystick.setRumble(RumbleType.kLeftRumble, intensity);
	}

	public double getAxisValue(int axis) {
		if (axis < 0) {
			return 0;
		}

		double sign = (axis == LEFT_LONGITUDIAL_AXIS || axis == RIGHT_LONGITUDIAL_AXIS) ? -1 : 1;
		return sign * removeOffset(joystick.getRawAxis(axis));
	}

	public int getPOV() {
		return joystick.getPOV();
	}
	
	public boolean getButton(int buttonValue) {	
		return joystick.getRawButton(buttonValue);
	}

	public int whenPressed(boolean isPressed) {
		if (isPressed == true) {
			return 1;
		} else {
			return 0;
		}
	}
}