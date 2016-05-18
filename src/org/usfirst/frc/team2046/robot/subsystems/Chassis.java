package org.usfirst.frc.team2046.robot.subsystems;

import java.util.concurrent.atomic.AtomicLong;

import org.usfirst.frc.team2046.robot.RobotMap;
import org.usfirst.frc.team2046.robot.commands.DelayedCallback;
import org.usfirst.frc.team2046.robot.commands.DriveCommand;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDControllerTest;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.TalonSRX;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Chassis extends Subsystem {
	private static final double SONAR_SCALE = 6603.6333945;

	private static final double SONAR_OFFSET = 1.3947262486;
	private static final double GYRO_SCALAR = .007;

	private static final double CHASSIS_WIDTH = 22.21/12;
	private double chassisWidth = CHASSIS_WIDTH;
	private static Chassis instance = new Chassis();;

	private final TalonSRX leftDrive1;
	private final TalonSRX leftDrive2;
	private final TalonSRX rightDrive1;
	private final TalonSRX rightDrive2;
	private final AnalogGyro gyro = new AnalogGyro(RobotMap.CHASSIS_GYRO);
	private final DoubleSolenoid shifter;
	private final Encoder leftEncoder;
	private final Encoder rightEncoder;
	private final Relay underglow;
	private final DigitalOutput lights;
	private boolean highGear = true;
	private final Compressor compressor;
	private final Counter sonarCounter;
	private boolean lightsOn = false;
	
	// TODO: Calibrate VisionAlign pixel/angle constant
	private static final double PIXELS_PER_DEGREE = 6.5;
	
	private double pixelsPerDegree = PIXELS_PER_DEGREE;
	private static final double HORIZONTAL_POWER = 1.6;
	private static final double FORWARD_POWER = 1.5;

	private static final double ON_TARGET = 0.5;

	private static final double ALMOST_ON_TARGET = 1.0;

	private double forwardPowerCurve = FORWARD_POWER;
	private double horizontalPowerCurve = HORIZONTAL_POWER;
	
	public double getForwardPowerCurve() {
		return forwardPowerCurve;
	}
	public double getHorizontalPowerCurve() {
		return horizontalPowerCurve;
	}
	private void initalize() {
		SmartDashboard.putNumber("Linear_P", linearController.getP());
		SmartDashboard.putNumber("Linear_I", linearController.getI());
		SmartDashboard.putNumber("Linear_D", linearController.getD());
		SmartDashboard.putNumber("Rotate_P", angleController.getP());
		SmartDashboard.putNumber("Rotate_I", angleController.getI());
		SmartDashboard.putNumber("Rotate_D", angleController.getD());
		SmartDashboard.putNumber("Pixels_Degree", pixelsPerDegree);
		SmartDashboard.putNumber("Forward Power Curve", forwardPowerCurve);
		SmartDashboard.putNumber("Horizontal Power Curve", horizontalPowerCurve);
		SmartDashboard.putNumber("Chassis Width", chassisWidth);
	}

	private Chassis() {
		leftDrive1 = new TalonSRX(RobotMap.CHASSIS_LEFT_DRIVE_1);
		leftDrive2 = new TalonSRX(RobotMap.CHASSIS_LEFT_DRIVE_2);
		rightDrive1 = new TalonSRX(RobotMap.CHASSIS_RIGHT_DRIVE_1);
		rightDrive2 = new TalonSRX(RobotMap.CHASSIS_RIGHT_DRIVE_2);

		leftDrive1.setInverted(true);
		leftDrive2.setInverted(true);
		
		gyro.setSensitivity(GYRO_SCALAR);
		gyro.calibrate();
		sonarCounter = new Counter(RobotMap.SONAR_SENSOR);
		sonarCounter.setSemiPeriodMode(true);

		leftEncoder = new Encoder(RobotMap.CHASSIS_LEFT_DRIVE_ENCODER_A, RobotMap.CHASSIS_LEFT_DRIVE_ENCODER_B, true);
		rightEncoder = new Encoder(RobotMap.CHASSIS_RIGHT_DRIVE_ENCODER_A, RobotMap.CHASSIS_RIGHT_DRIVE_ENCODER_B);
		leftEncoder.setDistancePerPulse(0.00200625);
		rightEncoder.setDistancePerPulse(0.00200625);

		compressor = new Compressor(RobotMap.ROBOT_PCM_1);
		compressor.start();
		
		underglow = new Relay(RobotMap.UNDERGLOW);
		underglow.set(Relay.Value.kForward);

		lights = new DigitalOutput(9);
		
		shifter = new DoubleSolenoid(RobotMap.ROBOT_PCM_2, RobotMap.CHASSIS_SHIFTER_A, RobotMap.CHASSIS_SHIFTER_B);
		initalize();
	}

	public void resetGyro() {
		gyro.reset();
	}

	public void recalibrateGyro() {
		gyro.calibrate();
	}

	public double getLeftDistance() {
		return leftEncoder.getDistance();
	}

	public double getRightDistance() {
		return rightEncoder.getDistance();
	}

	// TODO: add auto-turn off functionality to protect electronics
	public void setLights(boolean on){
		lightsOn = on;
		lights.set(on);
	}
	public double getAverageDistance() {
		return (leftEncoder.getDistance() + rightEncoder.getDistance()) / 2.0;
	}

	public void resetEncoders() {
		leftEncoder.reset();
		rightEncoder.reset();
	}

	public void resetSensors() {
		resetEncoders();
		resetGyro();
	}

	public double getChassisAngle() {
		return gyro.getAngle();
	}
	
	public double getChassisAngleFromEncoders() {
		
		return Math.toDegrees((leftEncoder.getDistance() - rightEncoder.getDistance())/chassisWidth);
	}

	public static Chassis getInstance() {
		return instance;
	}

	public double getSonar() {
		return sonarCounter.getPeriod();
	}

	public double getSonarDistance() {
		return sonarCounter.getPeriod() * SONAR_SCALE + SONAR_OFFSET;
	}

	public void updateDashboard() {
		SmartDashboard.putNumber("sonarCounter", getSonarDistance());
		SmartDashboard.putNumber("Drive_RightEncoder", getRightDistance());
		SmartDashboard.putNumber("Drive_LeftEncoder", getLeftDistance());
		SmartDashboard.putNumber("Drive_AverageEncoder", getAverageDistance());
		SmartDashboard.putNumber("Angle(gyro)", getChassisAngle());
		SmartDashboard.putNumber("Angle(encoders)", getChassisAngleFromEncoders());
		double targetAngle = getAngleToTarget();
		if (targetAngle != INVALID_ANGLE_TO_TARGET) {
			SmartDashboard.putNumber("Angle to Target", getAngleToTarget());
		}
		
		pixelsPerDegree = SmartDashboard.getNumber("Pixels_Degree", -10.7);
		forwardPowerCurve = SmartDashboard.getNumber("Forward Power Curve", forwardPowerCurve);
		horizontalPowerCurve = SmartDashboard.getNumber("Horizontal Power Curve", horizontalPowerCurve);
		chassisWidth = SmartDashboard.getNumber("Chassis Width", chassisWidth);

		boolean lOut = false, lOk = false, onTarget = false, rOk = false, rOut = false;
		if (targetAngle > ALMOST_ON_TARGET) {
			rOut = true;
		} else if (targetAngle < -ALMOST_ON_TARGET) {
			lOut = true;
		} else if (targetAngle > ON_TARGET) {
			rOk = true;
		} else if (targetAngle < -ON_TARGET){
			lOk = true;
		} else {
			onTarget = true;
		}
		SmartDashboard.putBoolean("Left Out", lOut);
		SmartDashboard.putBoolean("Left Ok", lOk);
		SmartDashboard.putBoolean("On Target", onTarget);
		SmartDashboard.putBoolean("Right Ok", rOk);
		SmartDashboard.putBoolean("Right Out", rOut);
	}
	
	public void updateAllTheTime() {
		boolean toggle = SmartDashboard.getBoolean("Toggle Lights", false);
		SmartDashboard.putBoolean("Toggle Lights", false);
		if (toggle) {
			setLights(!lightsOn);
		}		
	}

	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new DriveCommand());
	}

	public void setGear(boolean highGear) {
		this.highGear = highGear;
		if (highGear) {
			shifter.set(Value.kForward);
			Scheduler.getInstance().add(new DelayedCallback(0.2) {
				@Override
				public void onCallback() {
					if (instance.highGear) {
						shifter.set(Value.kOff);
					}
				}
			});
		} else {
			shifter.set(Value.kReverse);
			Scheduler.getInstance().add(new DelayedCallback(0.2) {
				@Override
				public void onCallback() {
					if (!instance.highGear) {
						shifter.set(Value.kOff);
					}
				}
			});
		}
	}

	public void setSpeed(double leftSpeed, double rightSpeed) {
		leftDrive1.set(leftSpeed);
		leftDrive2.set(leftSpeed);
		rightDrive1.set(rightSpeed);
		rightDrive2.set(rightSpeed);
	}

	private final AtomicLong linearSpeedCommand = new AtomicLong();
	private final AtomicLong rotationalSpeedCommand = new AtomicLong();
	
	private double getLinearSpeedCommand() {
		return Double.longBitsToDouble(linearSpeedCommand.get());
	}
	
	private double getRotationalSpeedCommand() {
		return Double.longBitsToDouble(rotationalSpeedCommand.get());
	}
	
	private void setLinearSpeedCommand(double linearSpeed) {
		setSpeedCommand(linearSpeed, getRotationalSpeedCommand());
	}

	private void setRotationalSpeedCommand(double rotationalSpeed) {
		setSpeedCommand(getLinearSpeedCommand(), rotationalSpeed);
	}

	private void setSpeedCommand(double linearSpeed, double rotationalSpeed) {
/*		System.out.printf("--setSpeedCommand-- %4.2f %5.2f %c | %4.2f %5.2f %c\n", 
				linearSpeed, getAverageDistance(), distanceOnTarget() ? '*' : ' ',
				rotationalSpeed, getChassisAngle(), angleOnTarget() ? '*' : ' ');
*/
		linearSpeedCommand.set(Double.doubleToLongBits(linearSpeed));
		rotationalSpeedCommand.set(Double.doubleToLongBits(rotationalSpeed));
		
		setSpeed(linearSpeed + rotationalSpeed, linearSpeed - rotationalSpeed);
	}

	private volatile boolean isAngleSlaved = false;
	private volatile double distanceToAngleSlaveValue = 0.0;
	
	private final PIDControllerTest linearController = new PIDControllerTest(0.4, 0.01, 1.0, new PIDSource() {
		@Override
		public void setPIDSourceType(PIDSourceType pidSource) {
		}

		@Override
		public PIDSourceType getPIDSourceType() {
			return PIDSourceType.kDisplacement;
		}

		@Override
		public double pidGet() {
			double distance = getAverageDistance();
			setSlavedRotationalSetpoint(distance);
			return distance;
		}
	}, new PIDOutput() {
		@Override
		public void pidWrite(double output) {
			setLinearSpeedCommand(output);
		}
	}, 0.02);

	private final PIDControllerTest angleController = new PIDControllerTest(0.05, 0.0, 0.0, new PIDSource() {
		@Override
		public void setPIDSourceType(PIDSourceType pidSource) {
		}

		@Override
		public PIDSourceType getPIDSourceType() {
			return PIDSourceType.kDisplacement;
		}

		@Override
		public double pidGet() {
			return getChassisAngle();
		}
	}, new PIDOutput() {
		@Override
		public void pidWrite(double output) {
			setRotationalSpeedCommand(output);
		}
	});

	public void setLinearSetpoint(double distance, double maxSpeed) {
		setSetpoints(distance, 0.0, maxSpeed, false);
	}

	public void setRotationalSetpoint(double angle, double maxSpeed) {
		setSetpoints(0.0, angle, maxSpeed, false);
	}

	public void setSetpoints(double distance, double angle, double maxSpeed) {
		setSetpoints(distance, angle, maxSpeed, distance != 0 && angle != 0);
	}
	
	public void setSetpoints(double distance, double angle, double maxSpeed, boolean isAngleSlaved) {
		if (distance == 0 && isAngleSlaved) {
			throw new IllegalArgumentException("distance == 0 && isAngleSlaved");
		}
		this.isAngleSlaved = isAngleSlaved;
		resetEncoders();
		resetGyro();
		setSpeed(0, 0);
		
		double p = SmartDashboard.getNumber("Linear_P", linearController.getP());
		double i = SmartDashboard.getNumber("Linear_I", linearController.getI());
		double d = SmartDashboard.getNumber("Linear_D", linearController.getD());
		linearController.setPID(p, i, d);

		p = SmartDashboard.getNumber("Rotate_P", angleController.getP());
		i = SmartDashboard.getNumber("Rotate_I", angleController.getI());
		d = SmartDashboard.getNumber("Rotate_D", angleController.getD());
		angleController.setPID(p, i, d);
		
		linearController.setOutputRange(-maxSpeed, maxSpeed);
		linearController.setSetpoint(distance);
		linearController.setAbsoluteTolerance(1.0 / 12.0); // 1/2 inch

		angleController.setOutputRange(-maxSpeed, maxSpeed);
		angleController.setSetpoint(isAngleSlaved ? 0 : angle);
		angleController.setAbsoluteTolerance(1.5); // 1/2 degree
		
		distanceToAngleSlaveValue = isAngleSlaved ? (angle / distance) : 0.0;

		linearController.enable();
		angleController.enable();
		
		//System.out.format("--setSetpoints-- %f %f %f %s %f\n", distance, angle, maxSpeed, (isAngleSlaved ? "slaved" : ""), distanceToAngleSlaveValue);
	}

	private void setSlavedRotationalSetpoint(double distance) {
		if (isAngleSlaved) {
			angleController.setSetpoint(distance * distanceToAngleSlaveValue);
			//System.out.format("--angles-- %f %f %f -- ", distance, distanceToAngleSlaveValue, angleController.getSetpoint());
		}
	}

	public boolean distanceOnTarget() {
		return linearController.onTarget();
	}

	public boolean angleOnTarget() {
		return angleController.onTarget();
	}

	public void disableControllers() {
		linearController.disable();
		angleController.disable();
	}
	public void disableLinearControllers() {
		linearController.disable();
	}
	
	public boolean isLinearControllerEnabled() {
		return linearController.isEnabled();
	}
	public void disableAngularControllers() {
		angleController.disable();
	}
	
	public static final double INVALID_ANGLE_TO_TARGET = -9999;
	public double getAngleToTarget() {
		double angle = INVALID_ANGLE_TO_TARGET;
		double[] centerX = {};
		double width = 0;
		
		NetworkTable reports = NetworkTable.getTable("GRIP/myContoursReport");
		NetworkTable imageSize = NetworkTable.getTable("GRIP/mySize");
		if (reports != null && imageSize != null) {
			centerX = reports.getNumberArray("centerX", centerX);
			width = imageSize.getNumber("x", 0.0);
		
			// must filter out contours to exactly one contour
			if (centerX.length == 1) {		
				angle = -(width / 2.0 - centerX[0]) / pixelsPerDegree;
			}
		}
		
		return angle;
	}
}