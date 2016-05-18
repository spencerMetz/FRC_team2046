package org.usfirst.frc.team2046.robot.subsystems;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.ImageType;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.vision.USBCamera;

/**
 *
 */
public class Vision extends Subsystem {
	private final USBCamera camera;
	private final CameraServer server;
	private static Vision instance = new Vision();

	private Vision() {
		camera = new USBCamera();
		server = CameraServer.getInstance();
	}

	public static Vision getInstance() {
		return instance;
	}

	public void initDefaultCommand() {
		VisionDefaultCommand testDefaultCommand = new VisionDefaultCommand();
		testDefaultCommand.setRunWhenDisabled(true);
		setDefaultCommand(testDefaultCommand);
	}

	class VisionDefaultCommand extends Command {
		NIVision.Image image;
		int n = 0;

		public VisionDefaultCommand() {
			requires(Vision.getInstance());
		}

		@Override
		protected void initialize() {
			camera.openCamera();
			camera.setExposureManual(10);
			camera.startCapture();
			image = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 3);
		}

		@Override
		protected void execute() {
			try {
				camera.getImage(image);
				server.setImage(image);
			} catch (Exception e) {
			}
		}

		@Override
		protected boolean isFinished() {
			return false;
		}

		@Override
		protected void end() {
			camera.closeCamera();
		}

		@Override
		protected void interrupted() {
			end();
		}

	}
}
