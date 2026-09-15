package com.bolinho.tracker;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
  final int BG=Color.rgb(8,10,16), CARD=Color.rgb(18,21,31), TEXT=Color.rgb(242,244,249), MUTED=Color.rgb(153,160,178), RED=Color.rgb(255,70,85), GREEN=Color.rgb(77,220,154);
  SharedPreferences p; LinearLayout body;
  int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);} 
  GradientDrawable bg(int c,int r){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(dp(r));return g;}
  TextView t(String s,int z,int c,boolean b){TextView v=new TextView(this);v.setText(s);v.setTextSize(z);v.setTextColor(c);v.setTypeface(Typeface.DEFAULT,b?1:0);return v;}
  Button b(String s){Button v=new Button(this);v.setText(s);v.setAllCaps(false);v.setTextColor(Color.WHITE);v.setBackground(bg(RED,14));return v;}
  EditText e(String h){EditText v=new EditText(this);v.setHint(h);v.setHintTextColor(MUTED);v.setTextColor(TEXT);v.setSingleLine(true);v.setBackground(bg(Color.rgb(25,29,42),12));v.setPadding(dp(12),dp(10),dp(12),dp(10));return v;}
  LinearLayout card(){LinearLayout x=new LinearLayout(this);x.setOrientation(LinearLayout.VERTICAL);x.setPadding(dp(16),dp(16),dp(16),dp(16));x.setBackground(bg(CARD,18));LinearLayout.LayoutParams q=new LinearLayout.LayoutParams(-1,-2);q.setMargins(0,0,0,dp(12));x.setLayoutParams(q);return x;}

  @Override public void onCreate(Bundle x){super.onCreate(x);getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);p=getSharedPreferences("bolinho",0);if(p.getString("pin","").isEmpty())createPin();else login();}
  void createPin(){LinearLayout r=root();r.setGravity(Gravity.CENTER);r.addView(t("BOLINHO // TRACKER",25,TEXT,true));r.addView(t("Seu hub pessoal de evolução no VALORANT",14,MUTED,false));LinearLayout c=card();c.addView(t("Crie seu PIN",20,TEXT,true));EditText a=e("PIN de 4 a 8 números"),d=e("Confirmar PIN");a.setInputType(18);d.setInputType(18);c.addView(a);c.addView(d);Button ok=b("Criar acesso");c.addView(ok);r.addView(c);setContentView(r);ok.setOnClickListener(v->{String s=a.getText().toString();if(s.length()<4||s.length()>8||!s.equals(d.getText().toString())){toast("Confira o PIN.");return;}p.edit().putString("pin",hash(s)).putString("riot","Bolinho #BR11").apply();seed();app();});}
  void login(){LinearLayout r=root();r.setGravity(Gravity.CENTER);r.addView(t("BOLINHO // TRACKER",25,TEXT,true));r.addView(t(p.getString("riot","Bolinho #BR11"),14,MUTED,false));LinearLayout c=card();c.addView(t("Bem-vindo de volta",20,TEXT,true));EditText a=e("PIN");a.setInputType(18);c.addView(a);Button ok=b("Entrar");c.addView(ok);r.addView(c);setContentView(r);ok.setOnClickListener(v->{if(hash(a.getText().toString()).equals(p.getString("pin","")))app();else toast("PIN incorreto.");});}
  LinearLayout root(){LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.VERTICAL);r.setBackgroundColor(BG);r.setPadding(dp(22),dp(24),dp(22),dp(24));return r;}

  void app(){LinearLayout r=root();LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);TextView h=t("BOLINHO // TRACKER",20,TEXT,true);h.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1));top.addView(h);Button lock=b("🔒");lock.setOnClickListener(v->login());top.addView(lock);r.addView(top);ScrollView sv=new ScrollView(this);body=new LinearLayout(this);body.setOrientation(LinearLayout.VERTICAL);body.setPadding(0,dp(14),0,0);sv.addView(body);sv.setLayoutParams(new LinearLayout.LayoutParams(-1,0,1));r.addView(sv);setContentView(r);dashboard();}
  void dashboard(){body.removeAllViews();body.addView(t("Seu painel",27,TEXT,true));body.addView(t("Treine com intenção. Jogue com cabeça. Evolua todo dia.",13,MUTED,false));score();stats();matchBox();missionBox();noteBox();profileBox();}
  void score(){LinearLayout c=card();c.addView(t("NOTA DO DIA",12,MUTED,true));int old=p.getInt("score_"+today(),-1);TextView n=t(old<0?"—/10":old+"/10",38,old>=8?GREEN:TEXT,true);c.addView(n);SeekBar s=new SeekBar(this);s.setMax(10);s.setProgress(old<0?7:old);c.addView(s);Button z=b("Salvar nota");z.setOnClickListener(v->{p.edit().putInt("score_"+today(),s.getProgress()).apply();dashboard();});c.addView(z);body.addView(c);}
  void stats(){JSONArray m=arr("matches"),q=arr("missions");int w=0,k=0,d=0,done=0;for(int i=0;i<m.length();i++){JSONObject o=m.optJSONObject(i);if(o!=null){if(o.optBoolean("win"))w++;k+=o.optInt("k");d+=o.optInt("d");}}for(int i=0;i<q.length();i++)if(q.optJSONObject(i)!=null&&q.optJSONObject(i).optBoolean("done"))done++;LinearLayout c=card();c.addView(t("RESUMO",12,MUTED,true));c.addView(t("Partidas: "+m.length()+"   •   Win rate: "+(m.length()==0?"—":Math.round(w*100f/m.length())+"%"),16,TEXT,true));c.addView(t("K/D: "+(d==0?"—":String.format(Locale.US,"%.2f",(double)k/d))+"   •   Missões: "+done+"/"+q.length(),14,MUTED,false));body.addView(c);}
  void matchBox(){LinearLayout c=card();c.addView(t("TRACKER DE PARTIDAS",12,MUTED,true));Button add=b("+ Registrar partida");add.setOnClickListener(v->matchDialog());c.addView(add);JSONArray a=arr("matches");for(int i=a.length()-1;i>=Math.max(0,a.length()-3);i--){JSONObject o=a.optJSONObject(i);c.addView(t((o.optBoolean("win")?"✓ Vitória":"✕ Derrota")+" • "+o.optString("agent")+" • "+o.optInt("k")+"/"+o.optInt("d")+"/"+o.optInt("a")+" • "+o.optInt("rr")+" RR",13,o.optBoolean("win")?GREEN:TEXT,false));}body.addView(c);}
  void matchDialog(){LinearLayout x=root();EditText ag=e("Agente"),ki=e("Kills"),de=e("Deaths"),as=e("Assists"),rr=e("RR (+18 ou -16)");ki.setInputType(2);de.setInputType(2);as.setInputType(2);rr.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);x.addView(ag);x.addView(ki);x.addView(de);x.addView(as);x.addView(rr);final String[] opts={"Vitória","Derrota"};Spinner sp=new Spinner(this);sp.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,opts));x.addView(sp);new AlertDialog.Builder(this).setTitle("Registrar partida").setView(x).setPositiveButton("Salvar",(d,w)->{try{JSONObject o=new JSONObject();o.put("agent",ag.getText().toString());o.put("k",num(ki));o.put("d",num(de));o.put("a",num(as));o.put("rr",num(rr));o.put("win",sp.getSelectedItemPosition()==0);JSONArray a=arr("matches");a.put(o);save("matches",a);dashboard();}catch(Exception z){}}).setNegativeButton("Cancelar",null).show();}
  void missionBox(){LinearLayout c=card();c.addView(t("MISSÕES",12,MUTED,true));Button add=b("+ Nova missão");add.setOnClickListener(v->missionDialog());c.addView(add);JSONArray a=arr("missions");for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);final int ix=i;Button m=b((o.optBoolean("done")?"✓ ":"")+o.optString("title"));m.setOnClickListener(v->{JSONArray z=arr("missions");JSONObject q=z.optJSONObject(ix);try{q.put("done",!q.optBoolean("done"));save("missions",z);}catch(Exception ex){}dashboard();});c.addView(m);}body.addView(c);}
  void missionDialog(){EditText x=e("Missão");new AlertDialog.Builder(this).setTitle("Nova missão").setView(x).setPositiveButton("Criar",(d,w)->{String s=x.getText().toString().trim();if(s.isEmpty())return;try{JSONArray a=arr("missions");JSONObject o=new JSONObject();o.put("title",s);o.put("done",false);a.put(o);save("missions",a);dashboard();}catch(Exception z){}}).setNegativeButton("Cancelar",null).show();}
  void noteBox(){LinearLayout c=card();c.addView(t("ANOTAÇÕES",12,MUTED,true));EditText n=e("Escreva um erro, call, ideia ou meta...");c.addView(n);Button add=b("Salvar anotação");add.setOnClickListener(v->{String s=n.getText().toString().trim();if(s.isEmpty())return;try{JSONArray a=arr("notes");a.put(s);save("notes",a);dashboard();}catch(Exception z){}});c.addView(add);JSONArray a=arr("notes");for(int i=a.length()-1;i>=Math.max(0,a.length()-3);i--)c.addView(t("• "+a.optString(i),13,MUTED,false));body.addView(c);}
  void profileBox(){LinearLayout c=card();c.addView(t("PERFIL",12,MUTED,true));EditText id=e("Riot ID");id.setText(p.getString("riot","Bolinho #BR11"));c.addView(id);Button s=b("Salvar Riot ID");s.setOnClickListener(v->{p.edit().putString("riot",id.getText().toString()).apply();toast("Perfil salvo.");});c.addView(s);body.addView(c);}
  void seed(){if(p.contains("missions"))return;try{JSONArray a=new JSONArray();String[] s={"Treinar mira com qualidade","Revisar uma partida","Jogar ranked com foco mental","Anotar 1 erro que se repetiu"};for(String x:s){JSONObject o=new JSONObject();o.put("title",x);o.put("done",false);a.put(o);}save("missions",a);}catch(Exception e){}}
  JSONArray arr(String k){try{return new JSONArray(p.getString(k,"[]"));}catch(Exception e){return new JSONArray();}}
  void save(String k,JSONArray a){p.edit().putString(k,a.toString()).apply();}
  int num(EditText x){try{return Integer.parseInt(x.getText().toString());}catch(Exception e){return 0;}}
  String today(){return new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date());}
  String hash(String s){try{MessageDigest m=MessageDigest.getInstance("SHA-256");byte[] b=m.digest(s.getBytes("UTF-8"));StringBuilder x=new StringBuilder();for(byte q:b)x.append(String.format("%02x",q));return x.toString();}catch(Exception e){return s;}}
  void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
}
