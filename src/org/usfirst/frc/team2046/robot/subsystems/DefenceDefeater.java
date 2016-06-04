package org.usfirst.frc.team2046.robot.subsystems;

import org.usfirst.frc.team2046.robot.OI;
import org.usfirst.frc.team2046.robot.RobotMap;
import org.usfirst.frc.team2046.robot.commands.CollectCommand;
import org.usfirst.frc.team2046.robot.commands.DelayedCallback;
import org.usfirst.frc.team2046.robot.operator.XboxController;
import org.usfirst.frc.team2046.robot.util.TripleSolenoid;
import org.usfirst.frc.team2046.robot.util.TripleSolenoid.Position;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.Subsystem;

// TODO: protect against over-current using the PDP measurements (current*time > threshold)
public class DefenceDefeater extends Subsystem {
	private static DefenceDefeater instance = new DefenceDefeater();
	private TripleSolenoid wedge;
	private final CANTalon collectorMotor1;
	private final CANTalon collectorMotor2;
	private DoubleSolenoid collectorPiston;
	private boolean collectorPistonPosition;
	private Position m_wedgePosition = TripleSolenoid.Position.RETRACT;
	private boolean collectorToggled;
	
	/**
	 * Synchronized signleton - runs initialize on initial getInstance() case
	 */
	private DefenceDefeater() {
		wedge = new TripleSolenoid(RobotMap.WEDGE_PORT_REVERSE, RobotMap.WEDGE_PORT_REVERSE_REVERSE, RobotMap.ROBOT_PCM_2, RobotMap.WEDGE_PORT_MID, RobotMap.ROBOT_PCM_2, RobotMap.WEDGE_PORT_FORWARD, RobotMap.ROBOT_PCM_2);
		collectorMotor1 = new CANTalon(RobotMap.COLLECTOR_MOTOR1);
		collectorMotor2 = new CANTalon(RobotMap.COLLECTOR_MOTOR2);
		collectorToggled = false;
		collectorMotor1.setInverted(false);
		collectorMotor2.setInverted(false);
		
		collectorPiston = new DoubleSolenoid(RobotMap.ROBOT_PCM_1, RobotMap.COLLECTOR_PORT_FORWARD, RobotMap.COLLECTOR_PORT_REVERSE);
		collectorPistonPosition = false;
	}

	/**
	 * Returns the wedge position from the enum Position - Position.RETRACT, Position.MID, Position.EXTEND
	 * @return Returns enum Position based on the current enum settings
	 */
	public Position getWedge() {
		return m_wedgePosition;
	}

	/**
	 * Gets a static instance of the DefenceDefeater.
	 * @return Singleton instance of DefenceDefeater.
	 */
	public static DefenceDefeater getInstance() {
		return instance;
	}
	
	/**
	 * Method Overloading. Sets collectorSpeed = collectorSpeed, default toggle is false.
	 * @param collectorSpeed Sets the collector speed between 0.0 and 1.0, corrected automatically if the value is below/above expected values.
	 */
	public void setSpeed(double collectorSpeed){
		collectorSpeed = Math.abs(collectorSpeed) > 1.0 ? 1.0 : Math.abs(collectorSpeed);
		setSpeed(collectorSpeed, false);
	}
	
	/**
	 * Sets the collector motor speed and the collector position.
	 * @param collectorSpeed Value between 0.0 and 1.0, corrected automatically if the value is below/above expected values.
	 * @param toggle Starts/Stops the DefenceDefeater.
	 */
	public void setSpeed(double collectorSpeed, boolean toggle) {
		double speed = collectorSpeed;
		if (toggle){
			if (collectorToggled){
				speed = 0.0;
			}
			collectorToggled = !collectorToggled;
		}
		
		if (Double.compare(speed, 0.0) == 0 && collectorPistonPosition){
			collectorPistonPosition = false;
			collectorPiston.set(DoubleSolenoid.Value.kReverse);
			
			Scheduler.getInstance().add(new DelayedCallback(0.5) {
				public void onCallback() {
					collectorPiston.set(Value.kOff);
				}
			});
		}
		else if (!collectorPistonPosition){
			collectorPistonPosition = true;
			collectorPiston.set(DoubleSolenoid.Value.kForward);
			
			Scheduler.getInstance().add(new DelayedCallback(0.5) {
				public void onCallback() {
					collectorPiston.set(Value.kOff);
				}
			});
		}
		
		collectorMotor1.set(speed);
		collectorMotor2.set(speed);
	}
	
	/**
	 * 
	 * @return
	 */
	public double getSpeed(){
		return collectorMotor1.get();
	}
	
	/**
	 * Determines if the DefenceDefeater is completely down
	 * @return If the wedgePosition is equal to Position.EXTEND, then the wedge is down.
	 */
	public boolean isDown(){
		return m_wedgePosition == TripleSolenoid.Position.EXTEND;
	}

	/**
	 * Command to start when this class is called.
	 */
	protected void initDefaultCommand() {
		setDefaultCommand(new DefenseDefeaterControl());
	}
	
	/**
	 * Checks if the DefenceDefeater is already commanded down or not.
	 * @param commanded Current command for positioning
	 * @return True = Wedge commanded down, False = Wedge not commanded down
	 */
	private boolean isWedgeCommandedDown(TripleSolenoid.Position commanded) {
		return ((m_wedgePosition == TripleSolenoid.Position.RETRACT && commanded != TripleSolenoid.Position.RETRACT) ||
				(m_wedgePosition == TripleSolenoid.Position.MID && commanded == TripleSolenoid.Position.EXTEND));
	}
	
	/**
	 * 
	 * @param position
	 */
	public void setWedge(TripleSolenoid.Position position) {
		if (Ballista.getInstance().isNotTransitioning() || isWedgeCommandedDown(position) || Interlock.getInstance().isOverriden()) {
			wedge.set(position);
			m_wedgePosition = position;
			Interlock.getInstance().setInterlockActivated(false);
		} else {
			System.out.println("Cannot bring up Defense Defeater while transitioning Ballista");

			Interlock.getInstance().setInterlockActivated(true);
		}
	}
	
	private class DefenseDefeaterControl extends Command{
		private double lastJoystickValue = 0.0;
		private double joystickValue = 0.0;
		private static final double Upper_Threshold = 0.8;
		private static final double Lower_Threshold = -0.8;
		
		/**
		 * Constructor, requires DefenceDefeater by default.
		 */
		public DefenseDefeaterControl() {
			requires(DefenceDefeater.getInstance());
		}
		
		/**
		 * Required method, unused.
		 */
		protected void initialize() {
		}
		
		/**
		 * Periodically runs - checks the value of manipulator's Left Y Axis and determines whether or not to move the DefenceDeafer - it made the Joystick's Left stick, Y axis, into a button.
		 */
		protected void execute() {
			
			joystickValue = OI.getInstance().getManipulatorJoystick().getAxisValue(XboxController.LEFT_LONGITUDIAL_AXIS);
			
			if(lastJoystickValue <= Upper_Threshold && joystickValue > Upper_Threshold){
				Scheduler.getInstance().add(new CollectCommand(1.0));
			}else if(lastJoystickValue >= Upper_Threshold && joystickValue < Upper_Threshold){
				Scheduler.getInstance().add(new CollectCommand(0.0));
			}else if(lastJoystickValue >= Lower_Threshold && joystickValue < Lower_Threshold){
				Scheduler.getInstance().add(new CollectCommand(-1.0));
			}else if(lastJoystickValue <= Lower_Threshold && joystickValue > Lower_Threshold){
				Scheduler.getInstance().add(new CollectCommand(0.0));
			}
			
			lastJoystickValue = joystickValue;
		}
		
		/**
		 * Always returns false, the command never ends!
		 */
		protected boolean isFinished() {
			return false;
		}
		
		/**
		 * Required method, unused.
		 */
		protected void end() {
		}
		
		/**
		 * Required method, unused.
		 */
		protected void interrupted() {
		}
	};
}