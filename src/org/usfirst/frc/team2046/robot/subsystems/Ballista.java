package org.usfirst.frc.team2046.robot.subsystems;

import org.usfirst.frc.team2046.robot.OI;
import org.usfirst.frc.team2046.robot.RobotMap;
import org.usfirst.frc.team2046.robot.commands.DefenceDefeaterCommand;
import org.usfirst.frc.team2046.robot.commands.DelayedCallback;
import org.usfirst.frc.team2046.robot.commands.ShootAndLowerCommand;
import org.usfirst.frc.team2046.robot.operator.XboxController;
import org.usfirst.frc.team2046.robot.util.TripleSolenoid;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDControllerTest;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Ballista extends Subsystem {
	private static Ballista instance = new Ballista();

	private final Solenoid piston;
	private final CANTalon shooterWheel;
	private final Encoder shooterEncoder;
	private final PIDControllerTest shooterPIDController;
	private final PIDSource shooterPIDSource;
	private final PIDOutput shooterPIDOutput;
	private boolean pistonStatus;

	// shoulder
	private final CANTalon shoulderDrive;
	private final Encoder shoulderOpticalEncoder;
	private final PIDController shoulderPIDController;
	private final PIDSource shoulderPIDSource;
	private final PIDOutput shoulderPIDOutput;


	private static final double PID_CONTROL_THRESHOLD_ANGLE = 10.0;
	private static final double SHOULDER_DRIVE_HOLD_DOWN_VALUE = -2.5 / 12;
	private double commandedShoulderAngle;
	private boolean shooterToggled;
	
	// public constants
	public static final double ANGLE_UP = 70.0;
	public static final double ANGLE_CLIMBING_UP = 78.0;
	public static final double ANGLE_DOWN = -1.0;
	public static final int SHOOTING_RPM = 4800;
	public static final int SHOOTING_CLIMBING_RPM = 3500;
	private double shoulderSetpoint = ANGLE_UP;

	// shooter linearize constants
	private static final double SHOOTER_STALL_CURRENT = 134.0;
	private static final double SHOOTER_MOTOR_RESISTANCE = 12.0 / SHOOTER_STALL_CURRENT;
	private static final double SHOOTER_EMF = (12.0 - SHOOTER_MOTOR_RESISTANCE * 0.7) / 18700;
	private static final double SHOOTER_GEAR_RATIO = 40.0 / 19;

	// shoulder linearize constants
	private static final double SHOULDER_WEIGHT = 11.31;
	private static final double SHOULDER_MOMENT_ARM = 17.09;
	private static final double SHOULDER_MOMENT_ANGLE_OFFSET = -11.59;
	private static final double SHOULDER_SPRING_ANGLE = 180.0;
	private static final double SHOULDER_SPRING_TORQUE = 42.86;
	private static final double SHOULDER_SPRING_QUANTITY = 3.0;
	private static final double SHOULDER_STALL_TORQUE = 12.4;
	private static final double SHOULDER_STALL_CURRENT = 86.0;
	private static final double SHOULDER_GEAR_RATIO = 24.0;
	private static final double SHOULDER_MOTOR_RESISTANCE = 12 / SHOULDER_STALL_CURRENT;

	private static final double ANGLE_DOWN_THRESHOLD =  5.0;
	private static final double ANGLE_UP_THRESHOLD   = 55.0;

	private static final double SHOULDER_DEGREES_PER_PULSE = 360.0 / 100.0 * 18.0 / 72.0;

	public enum PistonDirection {
		EXTEND, RETRACT, TOGGLE
	}

	public double getShoulderSetpoint() {
		return shoulderSetpoint;
	}

	private Ballista() {
		piston = new Solenoid(RobotMap.ROBOT_PCM_2, RobotMap.BALLISTA_PORT_FOWARD);
		pistonStatus = false;
		shooterWheel = new CANTalon(RobotMap.BALLISTA_WHEEL);

		// Shooter pid
		shooterEncoder = new Encoder(RobotMap.SHOOTER_ENCODER_A, RobotMap.SHOOTER_ENCODER_B, false, EncodingType.k1X);

		// shooterEncoder.setSamplesToAverage(127);

		shooterEncoder.setPIDSourceType(PIDSourceType.kRate);
		shooterEncoder.setDistancePerPulse(60.0 / 100.0);
		shooterPIDSource = new PIDSource() {
			public PIDSourceType getPIDSourceType() {
				return PIDSourceType.kRate;
			}

			public double pidGet() {
				return shooterEncoder.getRate()/1000;
			}

			public void setPIDSourceType(PIDSourceType arg0) {
			}
		};

		shooterPIDOutput = new PIDOutput() {
			public void pidWrite(double output) {
				shooterWheel.set(linearize(output, shooterEncoder.getRate()));
//				shooterWheel.set(-1.0);
			}

			private double linearize(double input, double rate) {
				double actualRate = rate * SHOOTER_GEAR_RATIO;
				double currentEMF = actualRate * SHOOTER_EMF;
				double output = currentEMF / 12;
				return input + output;
			}
		};

		shooterPIDController = new PIDControllerTest(0.08, 0.0, 0.7, shooterPIDSource, shooterPIDOutput);
		shooterPIDController.setInputRange(-7.0, 7.0);
		shooterPIDController.setOutputRange(-1, 1);
		shooterPIDController.setToleranceBuffer(15);
		shooterPIDController.setAbsoluteTolerance(0.15);

		// Shoulder
		shoulderDrive = new CANTalon(RobotMap.SHOULDER_DRIVE);
		shoulderDrive.setInverted(true);
		shoulderOpticalEncoder = new Encoder(RobotMap.SHOULDER_OPTICAL_ENCODER_A, RobotMap.SHOULDER_OPTICAL_ENCODER_B, true);
		shoulderOpticalEncoder.setDistancePerPulse(SHOULDER_DEGREES_PER_PULSE);
		commandedShoulderAngle = 0.0;

		// Shoulder PID
		shoulderPIDSource = new PIDSource() {
			public PIDSourceType getPIDSourceType() {
				return PIDSourceType.kDisplacement;
			}

			public double pidGet() {
				return getShoulderAngle();
			}

			public void setPIDSourceType(PIDSourceType arg0) {
			}
		};
		shoulderPIDOutput = new PIDOutput() {
			public void pidWrite(double output) {
				shoulderDrive.set(linearize(output, getShoulderAngle()));
			}

			private double linearize(double input, double currentAngle) {
				double torque = SHOULDER_WEIGHT * SHOULDER_MOMENT_ARM
						* Math.cos(Math.toRadians(currentAngle + SHOULDER_MOMENT_ANGLE_OFFSET));
				double springs = (SHOULDER_SPRING_ANGLE - currentAngle) / SHOULDER_SPRING_ANGLE * SHOULDER_SPRING_TORQUE
						* SHOULDER_SPRING_QUANTITY;
				double output = (((torque - springs) / SHOULDER_STALL_TORQUE * SHOULDER_STALL_CURRENT
						/ SHOULDER_GEAR_RATIO) * SHOULDER_MOTOR_RESISTANCE) / 12;
				return output + input;
			}
		};

		shoulderPIDController = new PIDController(0.03, 0.0, 0.1, shoulderPIDSource, shoulderPIDOutput);
		shoulderPIDController.setSetpoint(commandedShoulderAngle);
		shoulderPIDController.setOutputRange(-0.35, 0.35);
	}

	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new BallistaControl());
	}

	public static Ballista getInstance() {
		return instance;
	}
	
	 public void firePiston() {
		 piston.set(true);
		 Scheduler.getInstance().add(new DelayedCallback(0.5) {
			 public void onCallback() {
				 piston.set(false);
			 }
		 });
	 }

	public boolean getPistonExtended() {
		return pistonStatus;
	}

	public void setSpeed(double collectorSpeed) {
		setSpeed(collectorSpeed, false);
	}

	public void setSpeed(double speed, boolean toggle) {
		double tmpSpeed = speed;
		if (toggle) {
			if (shooterToggled) {
				tmpSpeed = 0.0;
			}
			shooterToggled = !shooterToggled;
		}
		shooterWheel.set(tmpSpeed);
	}

	/**
	 * Returns the shoulder angle after converting the analog encoder voltage
	 * 
	 * @return shoulder angle in degrees (horizontal is 0.0, vertical is 90.0)
	 */
	public double getShoulderAngle() {
		return shoulderOpticalEncoder.getDistance();
	}

	public void setShoulderAngle(double angle, boolean toggle) {
		if (toggle) {
			angle = commandedShoulderAngle == ANGLE_DOWN ? angle : ANGLE_DOWN;
		}
		setShoulderAngle(angle);
	}
	
	public void setShoulderAngle(double angle) {
		
		boolean isAdjusting = ((angle > ANGLE_UP_THRESHOLD && getShoulderAngle() > ANGLE_UP_THRESHOLD) ||
							   (angle < ANGLE_DOWN_THRESHOLD && getShoulderAngle() < ANGLE_DOWN_THRESHOLD));
		
		if (DefenceDefeater.getInstance().isDown() || isAdjusting || Interlock.getInstance().isOverriden()) {
			shoulderPIDController.setSetpoint(angle);
			commandedShoulderAngle = angle;
			Interlock.getInstance().setInterlockActivated(false);
		} else {
			System.out.println("Defense Defeater isn't down");
			Interlock.getInstance().setInterlockActivated(true);
		}
	}

	public double getCommandedShoulderAngle() {
		return commandedShoulderAngle;
	}

	public boolean isActuallyDown() {
		return getShoulderAngle() <= ANGLE_DOWN_THRESHOLD;
	}

	public boolean isCommandedDown() {
		return commandedShoulderAngle <= ANGLE_DOWN_THRESHOLD;
	}

	
	public boolean isDown() {
		return (getCommandedShoulderAngle() < ANGLE_DOWN_THRESHOLD && getShoulderAngle() < ANGLE_DOWN_THRESHOLD);
	}
	
	public boolean isUp() {
		return (getCommandedShoulderAngle() > ANGLE_UP_THRESHOLD && getShoulderAngle() > ANGLE_UP_THRESHOLD);		
	}
	
	public boolean isNotTransitioning() {
		return isDown() || isUp() ;
	}

	
	public void updateDashboard() {
		synchronized (shooterPIDController) {
			SmartDashboard.putNumber("Shooter Speed", shooterEncoder.getRate());
			SmartDashboard.putNumber("Shoulder Angle", getShoulderAngle());
			shoulderSetpoint = SmartDashboard.getNumber("Shoulder Setpoint", shoulderSetpoint);
		}
	}

	public void setRate(int rpm) {
		if (rpm == 0 || (isActuallyDown() && isCommandedDown())) {
			if (shooterPIDController.isEnabled()){
				shooterPIDController.disable();
				shooterWheel.set(0.0);
				shooterEncoder.reset();
				shooterPIDSource.setPIDSourceType(PIDSourceType.kRate);
			}
		} else {
			if (!shooterPIDController.isEnabled()){
				shooterPIDController.setSetpoint(rpm / -1000.0);
				shooterPIDController.enable();
				shooterEncoder.reset();
				shooterPIDSource.setPIDSourceType(PIDSourceType.kRate);
			}
		}
	}

	public boolean shooterOnTarget() {
		return shooterPIDController.onTarget();
	}

	private class BallistaControl extends Command {

		private double leftTriggerValue;
		private double rightTriggerValue;
		private double lastLeftTriggerValue;
		private double lastRightTriggerValue;
		private static final double Trigger_Threshold = 0.8;

		public BallistaControl() {
			requires(Ballista.getInstance());
		}

		@Override
		protected void end() {
		}

		@Override
		protected void execute() {
			
			if (commandedShoulderAngle < PID_CONTROL_THRESHOLD_ANGLE && getShoulderAngle() < PID_CONTROL_THRESHOLD_ANGLE) {
				if (shoulderPIDController.isEnabled()) {
					shoulderPIDController.disable();
				}
				if (shooterPIDController.isEnabled()){
					setRate(0);
				}
				shoulderDrive.set(SHOULDER_DRIVE_HOLD_DOWN_VALUE);
			}
			else if (!shoulderPIDController.isEnabled()) {
				shoulderPIDController.enable();
			}

			leftTriggerValue = OI.getInstance().getManipulatorJoystick().getAxisValue(XboxController.LEFT_TRIGGER_AXIS);
			rightTriggerValue = OI.getInstance().getManipulatorJoystick().getAxisValue(XboxController.RIGHT_TRIGGER_AXIS);

			if (lastLeftTriggerValue <= Trigger_Threshold && leftTriggerValue > Trigger_Threshold) {
				Scheduler.getInstance().add(new DefenceDefeaterCommand(TripleSolenoid.Position.EXTEND));
			}
			
			if (lastRightTriggerValue <= Trigger_Threshold && rightTriggerValue > Trigger_Threshold) {
				Scheduler.getInstance().add(new ShootAndLowerCommand());
			}
			
			if (shooterPIDController.onTarget() && shooterPIDController.isEnabled()){
				OI.getInstance().getManipulatorJoystick().setLeftRumble(0.5f);
				OI.getInstance().getManipulatorJoystick().setRightRumble(0.5f);
			}
			else{
				OI.getInstance().getManipulatorJoystick().setLeftRumble(0.0f);
				OI.getInstance().getManipulatorJoystick().setRightRumble(0.0f);
			}

			lastLeftTriggerValue = leftTriggerValue;
			lastRightTriggerValue = rightTriggerValue;

			shooterPIDController.setPID(SmartDashboard.getNumber("Shooter_kP"), SmartDashboard.getNumber("Shooter_kI"),
					SmartDashboard.getNumber("Shooter_kD"));
			shoulderPIDController.setPID(SmartDashboard.getNumber("Shoulder_kP"), SmartDashboard.getNumber("Shoulder_kI"),
					SmartDashboard.getNumber("Shoulder_kD"));
			
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void initialize() {
			SmartDashboard.putNumber("Shooter_kP", shooterPIDController.getP());
			SmartDashboard.putNumber("Shooter_kI", shooterPIDController.getI());
			SmartDashboard.putNumber("Shooter_kD", shooterPIDController.getD());
			SmartDashboard.putNumber("Shoulder_kP", shoulderPIDController.getP());
			SmartDashboard.putNumber("Shoulder_kI", shoulderPIDController.getI());
			SmartDashboard.putNumber("Shoulder_kD", shoulderPIDController.getD());
			SmartDashboard.putInt("Shooter_SetPoint", 4550);
			SmartDashboard.putNumber("Shoulder_SetPoint", shoulderSetpoint);
		}

		@Override
		protected void interrupted() {
			end();
		}

		@Override
		protected boolean isFinished() {
			return false;
		}
	}
}