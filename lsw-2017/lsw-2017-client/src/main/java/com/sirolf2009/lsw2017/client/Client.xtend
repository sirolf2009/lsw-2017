package com.sirolf2009.lsw2017.client

import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import xtendfx.FXApp
import javafx.scene.text.Text
import javafx.scene.image.ImageView
import javafx.scene.control.Label
import com.sirolf2009.lsw2017.client.net.Connector
import javafx.scene.image.Image
import com.sirolf2009.lsw2017.common.model.PointRequest

@FXApp class Client {

	override start(Stage it) throws Exception {
		title = "LSW 2017"
		userAgentStylesheet = STYLESHEET_CASPIAN

		val connector = new Connector()

		scene = new Scene(new StackPane => [
			children += new StackPane => [
				alignment = Pos.TOP_RIGHT
				children += new Label("") => [
					if(connector.connected.get) {
						graphic = new ImageView(new Image("green_dot.png"))
						text = "connected"
					} else {
						graphic = new ImageView(new Image("red_dot.png"))
						text = "disconnected"
					}
					connector.connected.addListener [ listener |
						if(connector.connected.get) {
							graphic = new ImageView(new Image("green_dot.png"))
							text = "connected"
						} else {
							graphic = new ImageView(new Image("red_dot.png"))
							text = "disconnected"
						}
					]
				]
			]

			val team = new TextField() => [
				maxWidth = 400
				requestFocus
			]
			val points = new TextField() => [
				maxWidth = 400
			]

			children += new VBox => [
				alignment = Pos.CENTER
				children += new HBox => [
					alignment = Pos.CENTER
					children += new Text("Team")
					children += team
				]
				children += new HBox => [
					alignment = Pos.CENTER
					children += new Text("Points")
					children += points
				]
				children += new Button => [
					text = "Submit"
					onAction = [
						connector.proxy.requestPoints(new PointRequest() => [
							teamName = team.text                       
							points = Integer.parseInt(points.text)
						])
						team.clear()
						points.clear()
					]
				]
			]
		], 800, 600)
		show()
	}

}
