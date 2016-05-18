package org.usfirst.frc.team2046.robot;

import org.usfirst.frc.team2046.robot.commands.ClimbCommand;
import org.usfirst.frc.team2046.robot.commands.DefenceDefeaterCommand;
import org.usfirst.frc.team2046.robot.commands.DeployHookCommand;
import org.usfirst.frc.team2046.robot.commands.DrivePrecisely;
import org.usfirst.frc.team2046.robot.commands.RaiseAndSpinupCommand;
import org.usfirst.frc.team2046.robot.commands.SetShooterRPMCommand;
import org.usfirst.frc.team2046.robot.commands.ShootLowerGoalCommand;
import org.usfirst.frc.team2046.robot.commands.ShoulderCommand;
import org.usfirst.frc.team2046.robot.commands.VisionAlignCommand;
import org.usfirst.frc.team2046.robot.operator.XboxController;
import org.usfirst.frc.team2046.robot.subsystems.Ballista;
import org.usfirst.frc.team2046.robot.util.TripleSolenoid;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	private static OI instance = new OI();
	private XboxController driverJoystick = new XboxController(0);
	private XboxController manipulatorJoystick = new XboxController(1);

	public static OI getInstance() {
		return instance;
	}

	private OI() {
		SmartDashboard.putNumber("Shooter_Angle", Ballista.ANGLE_UP);

		driverJoystick.buttonA.whenPressed(new VisionAlignCommand());
		driverJoystick.buttonY.whenPressed(new CommandGroup() {
			{
				addSequential(new VisionAlignCommand());
				addSequential(new WaitCommand(0.25));
				addSequential(new DrivePrecisely(3.5, 0.0, 0.6, 2.5));
			}
		});
		driverJoystick.leftBumper.whenPressed(new DefenceDefeaterCommand(TripleSolenoid.Position.MID));
		driverJoystick.rightBumper.whenPressed(new DefenceDefeaterCommand(TripleSolenoid.Position.EXTEND));

		manipulatorJoystick.leftBumper.whenPressed(new DefenceDefeaterCommand(TripleSolenoid.Position.MID));
		manipulatorJoystick.buttonStart.whenPressed(new DefenceDefeaterCommand(TripleSolenoid.Position.RETRACT));
		manipulatorJoystick.buttonBack.whenPressed(new ClimbCommand());
		manipulatorJoystick.rightBumper.whenPressed(new SetShooterRPMCommand(Ballista.SHOOTING_RPM));
		manipulatorJoystick.buttonX.whenPressed(new ShootLowerGoalCommand());
		manipulatorJoystick.buttonY.whenPressed(new ShoulderCommand(Ballista.getInstance().getShoulderSetpoint()));
		manipulatorJoystick.buttonB.whenPressed(new RaiseAndSpinupCommand(false));
		manipulatorJoystick.buttonA.whenPressed(new ShoulderCommand(0.0));
		manipulatorJoystick.buttonRightStick.whenPressed(new DeployHookCommand(true));
	}

	public XboxController getDriverJoystick() {
		return driverJoystick;
	}

	public XboxController getManipulatorJoystick() {
		return manipulatorJoystick;
	}
}