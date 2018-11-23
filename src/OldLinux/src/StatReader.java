import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class StatReader
{
	//可能需要做一个排序。。。？
	//先创建一个文件列表, 每个文件包含了若干函数
	public static TreeMap<String, String> funcCalledFile = new TreeMap<>();
	//调用函数a的文件x, 文件x中调用a的函数b, b在哪一行调用a, 以及调用的次数 
	public static void handle(ArrayList<ArrayList<callMessage>> ans)
	{
		
		for(ArrayList<callMessage> callStack:ans)
		{
			int len = callStack.size();
			if(len == 0)continue;
			String iFunc = callStack.get(0).callFunc;
			String iFile = callStack.get(0).callFile;
			for(int i=1;i<len;i++)
			{
				callMessage mess = callStack.get(i);
				
			}
		}
	}
	
	public static void ReadSourceFile(File f)
	{
		//读取原代码的信息
		//源代码是用{}配对的, 所以,,可以考虑逐个读入字符, 然后比较{}是否配对
		if(f.getName().toLowerCase().equals("makefile"))
		{
			return;
		}
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(f));
			char[] buf = new char[(int) f.length()];
			br.read(buf);
			int i = 0, p1 = 0, p2 = 0;
			int left = 0, right = 0;
			int last = 0;
			while(i<buf.length)
			{
				if(i+1<buf.length && buf[i] == '/' && buf[i+1] == '*')
				{
					while(i+1<=buf.length && (buf[i] != '*' || buf[i+1] !='/'))
					{
						i++;
					}
					i += 2;
					last = i;
				}
				else
				{
					switch(buf[i])
					{
					case '#':
						while(i<buf.length && buf[i] != '\n')
						{
							i++;
						}
						last = i++;
						break;
					case '{':
						left = 1;
						p2 = i++;
						while(i<buf.length && left > right)
						{
							if(buf[i] == '{')left++;
							else if(buf[i] == '}')right++;
							i++;
						}
						for(p1=p2; p1>=last && buf[p1]!=';'; p1--);
						for(int j=p1+1;j<p2;j++)
						{
							System.out.print(buf[j]);
						}
						//System.out.print("p1" + buf[p1] + (buf[p1] == '\n'));
						last = i;
						
						//{"int","static","void","struct","char","extern"};
					default:
						i++;
						break;
					}
					//System.out.print(buf[i++]);
				}
			}
			//System.out.println(f.length());
			//System.out.println(buf);
			//String s = buf.toString()
			/*String tp;
			String[] cs = {"int","static","void","struct","char"};
			
			while((tp=br.readLine().trim()) != null)
			{
				if(tp.length() == 0)continue;
				if(tp.charAt(0) == '#')continue;
				for(String i:cs)
				{
					if(tp.startsWith(i))
					{
						
					}
				}
			}*/
			br.close();
		}
		catch(IOException e)
		{
			
		}
	}
	public static ArrayList<ArrayList<callMessage>> ReadLogFile(File f)
	{
		//String lastPath = getLastPath(f);
		String tp,s;
		ArrayList<ArrayList<callMessage>> ans = new ArrayList<>();
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(f));
			while((s=br.readLine())!=null)
			{
				if(!s.contains("{"))
					continue;
				//System.out.println(s);
				br.readLine();
				StringBuilder input = new StringBuilder();
				while(s.length() > 0)
				{
					s = br.readLine();
					input.append(s);
				}
				StringTokenizer pound_tok = new StringTokenizer(input.toString(),"#");
				ArrayList<callMessage> nowCallStack = new ArrayList<>();
				while(pound_tok.hasMoreTokens())
				{
					tp = pound_tok.nextToken();
					//System.out.println(tp);
					//System.out.println("===============");
					StringTokenizer tok = new StringTokenizer(tp," ");
					String callFunc,callFile;
					int callLine;
					if(nowCallStack.isEmpty())
					{
						tok.nextToken();
						callFunc = tok.nextToken();
						nowCallStack.add(new callMessage(callFunc,null,0));
					}
					else
					{
						for(int i=0; i<3; i++)
							tok.nextToken();
						callFunc = tok.nextToken();
						if(callFunc.equals("??"))continue;
						//System.out.println(callFunc);
						while(tok.hasMoreTokens())
						{
							tp = tok.nextToken();
						}
						String[] tps = tp.split(":");
						callFile = tps[0];
						callLine = Integer.parseInt(tps[1]);
						nowCallStack.add(new callMessage(callFunc,callFile,callLine));
					}
				}
				//System.out.println(nowCallStack);
				ans.add(nowCallStack);
			}
			br.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
		return ans;
	}
	public static String getLastToken(String str, String delim)
	{
		StringTokenizer tok = new StringTokenizer(str, delim);
		String lastToken = "";
		while(tok.hasMoreTokens())
		{
			lastToken = tok.nextToken();
		}
		return lastToken;
	}

	public static void ReadFolder(String path, final int mode)
	{
		File f = null;
		File[] files = null;
		LinkedList<File> q = new LinkedList<>();
		LinkedList<File> myfiles = new LinkedList<>();
		q.add(new File(path));
		while(!q.isEmpty())
		{
			f = q.removeFirst();
			//System.out.println(f.getPath());
			if(f.isDirectory())
			{
				files = f.listFiles();
				for(File t:files)
				{
					if(t.isDirectory())
						q.addLast(t);
					else myfiles.addLast(t);
				}
			}
		}
		while((f=myfiles.pollFirst())!=null)
		{
			//System.out.println(f.getPath());
			switch(mode)
			{
			case 1:
				ReadLogFile(f);
				break;
			case 2:
				ReadSourceFile(f);
				break;
			default:
				break;
			}
			
		}
	}
}