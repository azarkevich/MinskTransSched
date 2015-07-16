using System;
using System.Collections.Generic;

namespace TranspotSchedParser
{
	class MainClass
	{
		public static void Main (string[] args)
		{
			var routesLines = System.IO.File.ReadAllLines(@"/home/sazarkevich/work.d/MinskTransSched/get-data/.tmp/routes.txt");

			var header = routesLines[0].Split(';');

			var indexes4Extract = new int[] {
				Array.IndexOf(header, "RouteNum"),
				Array.IndexOf(header, "RouteName"),
			};


			List<string> route = new List<string>();

			for (int i = 1; i < routesLines.Length; i++) {
				var routeLine = routesLines[i].Split(';');

				route.Clear();

				for (int j = 0; j < indexes4Extract.Length; j++) {
					route.Add(routeLine[indexes4Extract[j]]);
				}

				for (int j = 0; j < route.Count; j++) {
					Console.Write(route[j]);
				}
				Console.WriteLine();
			}
		}
	}
}
