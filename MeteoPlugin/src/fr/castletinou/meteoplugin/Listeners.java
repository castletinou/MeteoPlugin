package fr.castletinou.meteoplugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Listeners implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		ItemStack itemStack = event.getItem();

		if (itemStack == null) {
			return;
		}
//
		if (itemStack.getType() == Material.COMPASS && itemStack.hasItemMeta()
				&& itemStack.getItemMeta().hasDisplayName()
				&& itemStack.getItemMeta().getDisplayName().equalsIgnoreCase("§4§lMeteo Compass")) {
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
				try {

					String apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=48.86&longitude=2.33&hourly=temperature_2m&hourly=weathercode";

					URL url = new URL(apiUrl);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");

					int responseCode = connection.getResponseCode();
					if (responseCode == HttpURLConnection.HTTP_OK) {

						BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						StringBuilder response = new StringBuilder();
						String line;
						while ((line = reader.readLine()) != null) {
							response.append(line);
						}
						reader.close();

						JsonParser jsonParser = new JsonParser();
						JsonObject json = jsonParser.parse(response.toString()).getAsJsonObject();
						JsonObject hourlyData = json.getAsJsonObject("hourly");

						JsonArray temperatureArray = hourlyData.getAsJsonArray("temperature_2m");
						JsonArray weatherCodeArray = hourlyData.getAsJsonArray("weathercode");

						double latestTemperature = temperatureArray.get(temperatureArray.size() - 1).getAsDouble();
						int latestWeatherCode = weatherCodeArray.get(weatherCodeArray.size() - 1).getAsInt();

						String weather = getWeatherString(latestWeatherCode);

						String actionBarMessage = ChatColor.GRAY + "Temp: " + latestTemperature + "°C; Weather: "
								+ weather;

						sendActionBar(player, actionBarMessage);

					} else {
						player.sendMessage(ChatColor.RED + "Erreur lors de la récupération des données météo.");
					}

					connection.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
					player.sendMessage(ChatColor.RED + "Erreur lors de la récupération des données météo.");
				}
			}
		}
	}

	private String getWeatherString(int weatherCode) {
		switch (weatherCode) {
		case 0:
			return "Clear sky";
		case 1:
		case 2:
		case 3:
			return "Mainly clear, partly cloudy, and overcast";
		case 45:
		case 48:
			return "Fog and depositing rime fog";
		case 51:
		case 53:
		case 55:
			return "Drizzle: Light, moderate, and dense intensity";
		case 56:
		case 57:
			return "Freezing Drizzle: Light and dense intensity";
		case 61:
		case 63:
		case 65:
			return "Rain: Slight, moderate, and heavy intensity";
		case 66:
		case 67:
			return "Freezing Rain: Light and heavy intensity";
		case 71:
		case 73:
		case 75:
			return "Snow fall: Slight, moderate, and heavy intensity";
		case 77:
			return "Snow grains";
		case 80:
		case 81:
		case 82:
			return "Rain showers: Slight, moderate, and violent";
		case 85:
		case 86:
			return "Snow showers slight and heavy";
		case 95:
			return "Thunderstorm: Slight or moderate";
		case 96:
		case 99:
			return "Thunderstorm with slight and heavy hail";
		default:
			return "Unknown";
		}
	}

	private void sendActionBar(Player player, String message) {
		CraftPlayer craftPlayer = (CraftPlayer) player;
		IChatBaseComponent chatComponent = new ChatComponentText(message);
		PacketPlayOutChat packet = new PacketPlayOutChat(chatComponent, (byte) 2);
		craftPlayer.getHandle().playerConnection.sendPacket(packet);
	}
}
