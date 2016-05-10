package org.usfirst.frc.team2046.robot.autonomous;

import org.usfirst.frc.team2046.robot.commands.DefenceDefeaterCommand;
import org.usfirst.frc.team2046.robot.commands.autonomous.DefenseChevalDeFrise;
import org.usfirst.frc.team2046.robot.commands.autonomous.DefenseLowBar;
import org.usfirst.frc.team2046.robot.commands.autonomous.DefenseMoat;
import org.usfirst.frc.team2046.robot.commands.autonomous.DefensePortcullis;
import org.usfirst.frc.team2046.robot.commands.autonomous.DefenseRamparts;
import org.usfirst.frc.team2046.robot.commands.autonomous.DefenseRockWall;
import org.usfirst.frc.team2046.robot.commands.autonomous.DefenseRoughTerrain;
import org.usfirst.frc.team2046.robot.util.TripleSolenoid.Position;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DefenseChooser {

	private static final double DRIVE_DISTANCE = 10.0;
	private static final double DRIVE_SPEED = 0.7;
	
	private final SendableChooser defenseChooser = new SendableChooser();

	private enum Defense {
		LOW_BAR, PORTCULLIS, CHEVAL_DE_FRISE, MOAT, RAMPARTS, ROCK_WALL, ROUGH_TERRAIN, SKIP;
	}
	
	public void initialize() {
		defenseChooser.addDefault("Low Bar", 		Defense.LOW_BAR);
		defenseChooser.addObject("Portcullis",	 	Defense.PORTCULLIS);
		defenseChooser.addObject("Cheval de Frise", Defense.CHEVAL_DE_FRISE);
		defenseChooser.addObject("Moat", 			Defense.MOAT);
		defenseChooser.addObject("Ramparts", 		Defense.RAMPARTS);
		defenseChooser.addObject("Rock Wall", 		Defense.ROCK_WALL);
		defenseChooser.addObject("Rough Terrain", 	Defense.ROUGH_TERRAIN);
		defenseChooser.addObject("Skip (test only)",Defense.SKIP);
		
		SmartDashboard.putData("Defense Command", defenseChooser);
	}
	
	public Command getDefenseCommand() {
		Defense type = (Defense) defenseChooser.getSelected();
		Command cmd = null;
		
		switch (type) {
		
		case LOW_BAR:
			cmd = new DefenseLowBar(DRIVE_DISTANCE, DRIVE_SPEED);
			break;
			
		case PORTCULLIS:
			cmd = new DefensePortcullis(DRIVE_DISTANCE,DRIVE_SPEED);
			break;
			
		case CHEVAL_DE_FRISE:
			cmd = new DefenseChevalDeFrise(DRIVE_DISTANCE,DRIVE_SPEED);
			break;
			
		case MOAT:
			cmd = new DefenseMoat(DRIVE_DISTANCE,DRIVE_SPEED);
			break;
			
		case RAMPARTS:
			cmd = new DefenseRamparts(DRIVE_DISTANCE,DRIVE_SPEED);
			break;
			
		case ROCK_WALL:
			cmd = new DefenseRockWall(DRIVE_DISTANCE,DRIVE_SPEED);
			break;
			
		case ROUGH_TERRAIN:
			cmd = new DefenseRoughTerrain(DRIVE_DISTANCE,DRIVE_SPEED);
			break;
			
		case SKIP:
			cmd = new DefenceDefeaterCommand(Position.EXTEND);
			break;
		}
		
		return cmd;
	}

}
