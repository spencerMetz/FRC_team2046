package org.usfirst.frc.team2046.robot;

public class RobotMap 
{	
	public static final int ROBOT_PCM_1 = 1;
	public static final int ROBOT_PCM_2 = 2;
	public static final int CHASSIS_RIGHT_DRIVE_1 = 2;//PWM
	public static final int CHASSIS_RIGHT_DRIVE_2 = 3;
	public static final int CHASSIS_LEFT_DRIVE_1 = 0;
	public static final int CHASSIS_LEFT_DRIVE_2 = 1;
	public static final int CHASSIS_SHIFTER_A = 6;//pneumatic
	public static final int CHASSIS_SHIFTER_B = 2;
	public static final int CHASSIS_LEFT_DRIVE_ENCODER_A = 2;//digital
	public static final int CHASSIS_LEFT_DRIVE_ENCODER_B = 3;
	public static final int CHASSIS_RIGHT_DRIVE_ENCODER_A = 4;//digital
	public static final int CHASSIS_RIGHT_DRIVE_ENCODER_B = 5;
	public static final int CHASSIS_GYRO = 1;//analog
	
	public static final int BALLISTA_WHEEL = 11;//CAN
	public static final int BALLISTA_PORT_FOWARD = 3;//pneumatic
	public static final int BALLISTA_PORT_REVERSE = 7;
	public static final int BALLISTA_ENCODER = 0;
	
	public static final int WEDGE_PORT_FORWARD = 5;//pneumatic
	public static final int WEDGE_PORT_REVERSE_REVERSE = 4;
	public static final int WEDGE_PORT_MID = 1;
	public static final int WEDGE_PORT_REVERSE = 0;
	public static final int COLLECTOR_MOTOR1 = 12;//can
	public static final int COLLECTOR_MOTOR2 = 16;//can
	public static final int COLLECTOR_PORT_FORWARD = 0;//pneumatic
	public static final int COLLECTOR_PORT_REVERSE = 2;
	
	public static final int WINCH_MOTOR_1 = 13;//can
	public static final int WINCH_MOTOR_2 = 14;
	public static final int WINCH_MOTOR_3 = 15;
	public static final int SNAKER_MOTOR = 17;//can
	public static final int CLIMBER_PORT_FORWARD = 1;//pneumatic
	public static final int CLIMBER_PORT_REVERSE = 3;
		
	public static final int SHOULDER_DRIVE = 10;//can
	public static final int SHOOTER_ENCODER_A = 0;//digital
	public static final int SHOOTER_ENCODER_B = 1;
	
	public static final int SONAR_SENSOR = 8;//PWM	
	public static final int UNDERGLOW = 0;
	public static final int SHOULDER_OPTICAL_ENCODER_A = 6;
	public static final int SHOULDER_OPTICAL_ENCODER_B = 7;
	
	   
    // If you are using multiple modules, make sure to define both the port
    // number and the module. For example you with a rangefinder:
    // public static int rangefinderPort = 1;
    // public static int rangefinderModule = 1;
}