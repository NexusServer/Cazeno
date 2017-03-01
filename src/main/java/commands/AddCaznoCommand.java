package commands;

import java.util.HashMap;
import java.util.LinkedHashMap;

import cazeno.Cazno;
import cazeno.DataBase;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParameter;

public class AddCaznoCommand extends Command {
	public AddCaznoCommand() {
		super("caznoOperator", "카지노 관리", "/cazno <add|del|list>");
		this.setPermission("op");
		this.commandData.permission = "op";
		this.setCommandParameters(new LinkedHashMap<String, CommandParameter[]>() {
			{
				put("default",
						new CommandParameter[] { new CommandParameter("add", CommandParameter.ARG_TYPE_RAW_TEXT, false),
								new CommandParameter("금액", CommandParameter.ARG_TYPE_INT, false),
								new CommandParameter("확률", CommandParameter.ARG_TYPE_INT, false) });
				put("del", new CommandParameter[] { new CommandParameter("del", false) });
				put("list", new CommandParameter[] { new CommandParameter("list") });
			}
		});

	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		switch (args[0]) {
		case "add":
			int money = Integer.parseInt(args[1]);
			int psent = Integer.parseInt(args[2]);
			HashMap<String, Object> data = new HashMap<String, Object>() {
				{
					put("money", money);
					put("psent", psent);
				}
			};
			Cazno.addList.put((Player) sender, data);
			sender.sendMessage(" 설치할 공간을 터치해주세요");
			return true;

		case "del":
			break;
		case "list":
			break;
		}

		return false;
	}
}
