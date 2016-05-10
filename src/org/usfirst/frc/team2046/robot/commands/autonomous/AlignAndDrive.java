package org.usfirst.frc.team2046.robot.commands.autonomous;

import org.usfirst.frc.team2046.robot.commands.VisionAlignCommand;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AlignAndDrive extends CommandGroup {
	public AlignAndDrive() {
		addSequential(new VisionAlignCommand());
		addSequential(new DriveForwardCommand(2.91,0.5));
	}
}
