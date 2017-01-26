package org.usfirst.frc.team2046.robot.subsystems;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.ImageType;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.vision.USBCamera;

//Note: This class was never used during operation - GRIP was the replacement
public class Vision extends Subsystem {
	private final USBCamera camera;
	private final CameraServer server;
	private static Vision instance = new Vision();
	
	/**
	 * Instance method which creates a camera and CameraServer connection
	 */
	private Vision() {
		camera = new USBCamera();
		server = CameraServer.getInstance();
	}
	
	/**
	 * Returns 'this' instance of Vision
	 * @return Returns a singleton instance of Vision
	 */
	public static Vision getInstance() {
		return instance;
	}

	/**
	 * This command initializes the CameraServer using some 'default' settings
	 */
	public void initDefaultCommand() {
		VisionDefaultCommand testDefaultCommand = new VisionDefaultCommand();
		testDefaultCommand.setRunWhenDisabled(true);
		setDefaultCommand(testDefaultCommand);
	}

	class VisionDefaultCommand extends Command {
		NIVision.Image image;
		int n = 0;
		
		/**
		 * Constructor, forces requirement of the Vision class file.
		 */
		public VisionDefaultCommand() {
			requires(Vision.getInstance());
		}

		/**
		 * Initializes the camera and starts capture.
		 */
		protected void initialize() {
			camera.openCamera();
			camera.setExposureManual(10);
			camera.startCapture();
			image = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 3);
		}

		/**
		 * Execution command to obtain and send the Image using NetworkTables.
		 */
		protected void execute() {
			try {
				camera.getImage(image);
				server.setImage(image);
			} catch (Exception e) {
			}
		}

		/**
		 * This class file never ends, the camera server does not have a default 'off' switch, unless the camera is unplugged.
		 */
		protected boolean isFinished() {
			return false;
		}

		/**
		 * Closes the camera connection.
		 */
		protected void end() {
			camera.closeCamera();
		}

		/**
		 * Interruptions close the camera connection.
		 */
		protected void interrupted() {
			end();
		}

	}
}
