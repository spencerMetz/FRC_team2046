package org.usfirst.frc.team2046.robot.autonomous;

import org.usfirst.frc.team2046.robot.commands.DrivePrecisely;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// TODO: test required position for next week
public class PositionChooser {

	private enum DefensePosition {
		FAR_LEFT, MID_LEFT, MIDDLE, MID_RIGHT, FAR_RIGHT;
	}

	private enum GoalPosition {
		LEFT, MIDDLE, RIGHT;
	}

	private enum Positions {
		FAR_LEFT_TO_LEFT_GOAL(DefensePosition.FAR_LEFT,	GoalPosition.LEFT), 
		MID_LEFT_TO_LEFT_GOAL(DefensePosition.MID_LEFT, GoalPosition.LEFT),
		MID_LEFT_TO_MIDDLE_GOAL(DefensePosition.MID_LEFT, GoalPosition.MIDDLE), 
		MIDDLE_TO_MIDDLE_GOAL(DefensePosition.MIDDLE, GoalPosition.MIDDLE), 
		MID_RIGHT_TO_MIDDLE_GOAL(DefensePosition.MID_RIGHT, GoalPosition.MIDDLE),
		MID_RIGHT_TO_RIGHT_GOAL(DefensePosition.MID_RIGHT, GoalPosition.RIGHT), 
		FAR_RIGHT_TO_RIGHT_GOAL(DefensePosition.FAR_RIGHT, GoalPosition.RIGHT);

		public final DefensePosition defense;
		public final GoalPosition goal;

		private Positions(DefensePosition defense, GoalPosition goal) {
			this.defense = defense;
			this.goal = goal;
		}
	}

	private static final double INITIAL_WALL_DISTANCE = 176.0 / 12.0;
	private static final double FINAL_RADIUS = 90.0 / 12.0;
	private static final double LEFT_WALL_DISTANCE = 170.5 / 12.0;
	private static final double DEFENSE_SLOT_SPACING = 53 / 12.0;
	private static final double MAX_SPEED = 0.5;

	private final SendableChooser positionChooser = new SendableChooser();
	private boolean centerSelected;

	public void initialize() {
		positionChooser.addDefault("Far-Left (1) to Left Goal", Positions.FAR_LEFT_TO_LEFT_GOAL);
		positionChooser.addObject("Mid-Left (2) to Left Goal", Positions.MID_LEFT_TO_LEFT_GOAL);

		positionChooser.addObject("Mid-Left (2) to Middle Goal", Positions.MID_LEFT_TO_MIDDLE_GOAL);
		positionChooser.addObject("Middle   (3) to Middle Goal", Positions.MIDDLE_TO_MIDDLE_GOAL);
		positionChooser.addObject("Mid-Right(4) to Middle Goal", Positions.MID_RIGHT_TO_MIDDLE_GOAL);

		positionChooser.addObject("Mid-Right(4) to Right Goal", Positions.MID_RIGHT_TO_RIGHT_GOAL);
		positionChooser.addObject("Far-Right(5) to Right Goal", Positions.FAR_RIGHT_TO_RIGHT_GOAL);
		SmartDashboard.putData("Goal Position", positionChooser);
	}

	public Command getPositionCommand() {

		Positions positions = (Positions) positionChooser.getSelected();
		DefensePosition defensePosition = positions.defense;
		GoalPosition goalPosition = positions.goal;

		double slot = 0;
		switch (defensePosition) {
		case FAR_LEFT:
			slot = 0;
			break;
		case MID_LEFT:
			slot = 1;
			break;
		case MIDDLE:
			slot = 2;
			break;
		case MID_RIGHT:
			slot = 3;
			break;
		case FAR_RIGHT:
			slot = 4;
			break;
		}

		// distance from the goal center (back wall)
		double xi = -LEFT_WALL_DISTANCE + (slot + 0.5) * DEFENSE_SLOT_SPACING;
		double yi = -INITIAL_WALL_DISTANCE;

		double ai = 90;
		double af = 0.0;
		double dir = 0;
		centerSelected = false;
		boolean oneTurnOnly = false;
		switch (goalPosition) {
		case LEFT:
			oneTurnOnly = (defensePosition == DefensePosition.FAR_LEFT);
			af = 30.0;
			dir = 1;
			break;

		case MIDDLE:
			centerSelected = true;
			af = 90.0;
			switch (defensePosition) {
			case FAR_LEFT:
			case MID_LEFT:
			case MIDDLE:
				dir = -1;
				break;
			case MID_RIGHT:
			case FAR_RIGHT:
				dir = 1;
				break;
			}
			break;

		case RIGHT:
			af = 150.0;
			dir = -1;
			break;
		}

		// distance from the goal center (back wall)
		double xf = -FINAL_RADIUS * Math.cos(Math.toRadians(af));
		double yf = -FINAL_RADIUS * Math.sin(Math.toRadians(af));

		double r = 0;
		double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
		double xc, yc, l1, l2, theta1, theta2;
		
		if (oneTurnOnly) {
			theta2 = ai - af;
			r = (xf-xi)/(1 - Math.cos(Math.toRadians(theta2)));
			double addY = (yf-yi) - r * Math.sin(Math.toRadians(theta2)) -0.75;
			
			CommandGroup cmd = new CommandGroup();
			cmd.addSequential(new DrivePrecisely(addY, 0.0, MAX_SPEED));
			cmd.addSequential(new DrivePrecisely(r * Math.toRadians(theta2), dir * theta2, MAX_SPEED));

			return cmd;
		}
		
		for (int i = 0; i < 1000; i++) {
			x1 = getX(xi, ai, r, dir);
			y1 = getY(yi, ai, r, dir);
			x2 = getX(xf, af, r, -dir);
			y2 = getY(yf, af, r, -dir);
			double newR = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) / 2;
			double dR = Math.abs(r - newR);
			r = newR;

			if (dR < 0.001) {
				break;
			}
		}

		xc = (x1 + x2) / 2;
		yc = (y1 + y2) / 2;

		l1 = Math.sqrt(Math.pow(xc - xi, 2) + Math.pow(yc - yi, 2));
		l2 = Math.sqrt(Math.pow(xc - xf, 2) + Math.pow(yc - yf, 2));

		theta1 = Math.toDegrees((Math.acos((2 * r * r - (l1 * l1)) / (2 * r * r))));
		theta2 = Math.toDegrees((Math.acos((2 * r * r - (l2 * l2)) / (2 * r * r))));

//		System.out.printf("Cmd xi: %4.1f, yi: %4.1f xf: %4.1f yf: %4.1f\n", xi * 12, yi * 12, xf * 12, yf * 12);
//		System.out.printf("Cmd x1: %4.1f, y1: %4.1f x2: %4.1f y2: %4.1f\n", x1 * 12, y1 * 12, x2 * 12, y2 * 12);
//		System.out.printf("Cmd dir: %f, radius%4.1f theta1: %4.1f theta2 %4.1f\n", dir, r * 12, theta1, theta2);

		CommandGroup cmd = new CommandGroup();
		cmd.addSequential(new DrivePrecisely(r * Math.toRadians(theta1), -dir * theta1, MAX_SPEED));
		cmd.addSequential(new DrivePrecisely(r * Math.toRadians(theta2), dir * theta2, MAX_SPEED));

		return cmd;
	}

	private double getX(double x, double a, double r, double dir) {
		return x + r * Math.cos(Math.toRadians(a + dir * 90.0));
	}

	private double getY(double y, double a, double r, double dir) {
		return y + r * Math.sin(Math.toRadians(a + dir * 90.0));
	}

	public boolean isCenterGoal() {
		return centerSelected;
	}
}
