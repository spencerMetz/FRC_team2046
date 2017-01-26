package org.usfirst.frc.team2046.robot.commands;

import org.usfirst.frc.team2046.robot.subsystems.Chassis;

import edu.wpi.first.wpilibj.PIDControllerTest;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Command;

public class VisionAlignCommand extends Command {
	private boolean turnStarted;
	private PIDControllerTest anglePIDController;

	public VisionAlignCommand() {
		super("VisionAlign", 2.0);
	}

	protected void initialize() {
		Chassis.getInstance().setLights(true);
		Chassis.getInstance().resetGyro();
		turnStarted = false;
	}

	@Override
	protected void execute() {

		double angle = Chassis.getInstance().getAngleToTarget();

		if (angle != Chassis.INVALID_ANGLE_TO_TARGET && !turnStarted) {
			turnStarted = true;
			startTurn(angle, 1.0);
		}
	}

	private void startTurn(double angle, final double speed) {
		System.out.printf("Vision Align Angle Turn %f\n", angle);

		PIDSource anglePIDSource;
		PIDOutput anglePIDOutput;
		Chassis.getInstance().resetGyro();

		anglePIDSource = new PIDSource() {
			@Override
			public void setPIDSourceType(PIDSourceType pidSource) {
			}

			@Override
			public double pidGet() {
				return Chassis.getInstance().getChassisAngle();
			}

			@Override
			public PIDSourceType getPIDSourceType() {
				return PIDSourceType.kDisplacement;
			}
		};
		anglePIDOutput = new PIDOutput() {

			@Override
			public void pidWrite(double output) {
				Chassis.getInstance().setSpeed(output, -output);
			}
		};
		anglePIDController = new PIDControllerTest(0.1, 0.001, 0.14, anglePIDSource, anglePIDOutput, 0.02);
		anglePIDController.setSetpoint(angle);
		anglePIDController.setOutputRange(-speed, speed);
		anglePIDController.setAbsoluteTolerance(0.5);
		anglePIDController.setToleranceBuffer(3);
		anglePIDController.enable();
	}

	private boolean onTarget() {
		return anglePIDController != null && anglePIDController.isEntirelyOnTarget();
	}

	@Override
	protected boolean isFinished() {
//		if (onTarget()) {
//			System.out.println(Chassis.getInstance().getChassisAngle());
//		}

		return (turnStarted && onTarget()) || isTimedOut();
	}

	@Override
	protected void end() {
		if (isTimedOut()) {
			System.out.println("Vision timed out");
		}

		if (anglePIDController != null) {
			anglePIDController.disable();
		}

		System.out.printf("Final Angle %f, Target:%f\n", Chassis.getInstance().getChassisAngle(),
				Chassis.getInstance().getAngleToTarget());
				
		Chassis.getInstance().setLights(false);
		// Chassis.getInstance().resetGyro();
	}

	@Override
	protected void interrupted() {
		System.out.println("Vision interrupted");
		end();
	}

}
