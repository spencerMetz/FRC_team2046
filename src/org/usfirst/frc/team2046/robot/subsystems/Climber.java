package org.usfirst.frc.team2046.robot.subsystems;

import org.usfirst.frc.team2046.robot.OI;
import org.usfirst.frc.team2046.robot.RobotMap;
import org.usfirst.frc.team2046.robot.commands.DelayedCallback;
import org.usfirst.frc.team2046.robot.operator.XboxController;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Climber extends Subsystem {

//	private final PowerDistributionPanel pdp = new PowerDistributionPanel();
	private final DoubleSolenoid climberPiston;
	private final CANTalon winchMotor1;
	private final CANTalon winchMotor2;
	private final CANTalon winchMotor3;
	private final CANTalon snakerMotor;
		
	private static final Climber instance = new Climber();
	
	private boolean climberActuated = false;
	
	public static Climber getInstance(){
		return instance;
	}
	
	public Climber(){
		climberPiston = new DoubleSolenoid(RobotMap.ROBOT_PCM_1, RobotMap.CLIMBER_PORT_FORWARD, RobotMap.CLIMBER_PORT_REVERSE);
		winchMotor1 = new CANTalon(RobotMap.WINCH_MOTOR_1);
		winchMotor2 = new CANTalon(RobotMap.WINCH_MOTOR_2);
		winchMotor3 = new CANTalon(RobotMap.WINCH_MOTOR_3);
		winchMotor1.setInverted(true);
		winchMotor2.setInverted(true);
		winchMotor3.setInverted(true);
		snakerMotor = new CANTalon(RobotMap.SNAKER_MOTOR);
	}
	
	public void actuateClimber(boolean upwards){
		
		if (DefenceDefeater.getInstance().isDown() || Interlock.getInstance().isOverriden()) {
			
			climberPiston.set((upwards) ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
			
			climberActuated = upwards;
			
			Scheduler.getInstance().add(new DelayedCallback(0.5) {
				public void onCallback() {
					climberPiston.set(Value.kOff);
				}
			});
			Interlock.getInstance().setInterlockActivated(false);

		} else {
			System.out.println("Climber can't actuate due to DefenceDefeater not being down");
			Interlock.getInstance().setInterlockActivated(true);
		}
	}
	
	public void setSnakerSpeed(double speed){
		if (climberActuated || Interlock.getInstance().isOverriden()) {
			snakerMotor.set(speed);
			Interlock.getInstance().setInterlockActivated(false);
		} else if (speed != 0.0) {
			Interlock.getInstance().setInterlockActivated(true);
			System.out.println("Snake prevented due to not being activated");
		}
	}
	
	public void setWinchSpeed(double speed){
		winchMotor1.set(speed);
		winchMotor2.set(speed);
		winchMotor3.set(speed);
	}
	
	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new ClimberControl());
	}
	
	private class ClimberControl extends Command{

		public ClimberControl(){
			requires(Climber.getInstance());
		}
		
		@Override
		protected void initialize() {
		}
		private double deadband(double value,double deadband){
			return (Math.abs(value)>deadband)?value:0.0;
			}
		@Override
		protected void execute() {
			Climber.getInstance().setSnakerSpeed(deadband(OI.getInstance().getManipulatorJoystick().getAxisValue(XboxController.RIGHT_LONGITUDIAL_AXIS),20.0/127.0));
			Climber.getInstance().setWinchSpeed(OI.getInstance().getDriverJoystick().getAxisValue(XboxController.LEFT_TRIGGER_AXIS));
		}

		@Override
		protected boolean isFinished() {
			return false;
		}

		@Override
		protected void end() {
		}

		@Override
		protected void interrupted() {
			end();
		}
	}

	
	public void updateDashboard() {
//		double p1 = pdp.getCurrent(2);
//		double p2 = pdp.getCurrent(3);
//		double p3 = pdp.getCurrent(5);
//		System.out.printf("Power %5.1f %5.1f %5.1f\n", p1, p2, p3);
	}
}
