package sp;

import static java.lang.Character.isDigit;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
 
public class Sp {
    
    public static void main(String[] args) {
        //INTEGERS BITS OF HEXA CODE
        int D=0, W=0;
        //EXTRA INTEGER BITS USED FOR COMPUTATIONS
        int  D_done=0, data_bits=0, source_spCase=0, desti_spCase=0, bp_exc=0, immediate=0, scaled=0, mode_16 = 0, mode_32 = 0,special=0, source_sp=0, desti_sp=0, source_found=0, desti_found=0, check_plus1=-1;
        //STRING BITS OF HEXA CODE
        String mov_opcode="0",MOD="0",  REG = null, RM=null,data_h=null, data_l=null, addr_h=null, addr_l=null, prefix_reg=null, prefix_addr=null, base_code=null,scale_code=null ;
        //STRINGS BITS USED FOR COMPUTATIONS
        String  base_str=null, scale_str=null, source = null, desti=null, reg=null, index_code=null, index_str=null;
        String check_specialCase_desti=null, check_specialCase_source=null, check_blank=null, bin=null, check_char1=null;
        
        //DATA REQUIRED
        //Map of conversion of binary to hex code
         Map Hex = new HashMap();
         Hex.put("0000", "0");
         Hex.put("0001", "1");
         Hex.put("0010", "2");
         Hex.put("0011", "3");
         Hex.put("0100", "4");
         Hex.put("0101", "5");
         Hex.put("0110", "6");
         Hex.put("0111", "7");
         Hex.put("1000", "8");
         Hex.put("1001", "9");
         Hex.put("1010", "A");
         Hex.put("1011", "B");
         Hex.put("1100", "C");
         Hex.put("1101", "D");
         Hex.put("1110", "E");
         Hex.put("1111", "F");
    
        //Reg and R/M when MOD=11 assignments---Map Registers
        Map Register8 = new HashMap();
        Register8.put("AL", "000");
        Register8.put("CL", "001");
        Register8.put("DL", "010");
        Register8.put("BL", "011");
        Register8.put("AH", "100");
        Register8.put("CH", "101");
        Register8.put("DH", "110");
        Register8.put("BH", "111");
        
        Map Register16 = new HashMap();
        Register16.put("AX", "000");
        Register16.put("CX", "001");
        Register16.put("DX", "010");
        Register16.put("BX", "011");
        Register16.put("SP", "100");
        Register16.put("BP", "101");
        Register16.put("SI", "110");
        Register16.put("DI", "111");
        
        Map Register32 = new HashMap();
        Register32.put("EAX", "000");
        Register32.put("ECX", "001");
        Register32.put("EDX", "010");
        Register32.put("EBX", "011");
        Register32.put("ESP", "100");
        Register32.put("EBP", "101");
        Register32.put("ESI", "110");
        Register32.put("EDI", "111");
      
        //Map for special adrressing modes
        Map SpecialMode1 = new HashMap();//16 bit addr modes
        SpecialMode1.put("[BX+SI]", "000");
        SpecialMode1.put("[BX+DI]", "001");
        SpecialMode1.put("[BP+SI]", "010");
        SpecialMode1.put("[BP+DI]", "011");
        SpecialMode1.put("[SI", "100");
        SpecialMode1.put("[DI", "101");
        SpecialMode1.put("[BP", "110");       //special case
        SpecialMode1.put("[BX", "111");
        SpecialMode1.put("[BX+SI", "000");
        SpecialMode1.put("[BX+DI", "001");
        SpecialMode1.put("[BP+SI", "010");
        SpecialMode1.put("[BP+DI", "011");
       
        //32-bit addressing modes selected by R/M
        Map  SpecialMode2 = new HashMap();
        SpecialMode2.put("[EAX", "000");
        SpecialMode2.put("[ECX", "001");
        SpecialMode2.put("[EDX", "010");
        SpecialMode2.put("[EBX", "011");
       // SpecialMode2.put("[EAX]", "100");//scaled
        SpecialMode2.put("[EBP", "101");  //special cases
        SpecialMode2.put("[ESI", "110");
        SpecialMode2.put("[EDI", "111");
        
        //Segment Registers selection
        Map SegmentReg = new HashMap();
        SegmentReg.put("ES", "000");
        SegmentReg.put("CS", "001");
        SegmentReg.put("SS", "010");
        SegmentReg.put("DS", "011");
        SegmentReg.put("FS", "100");
        SegmentReg.put("GS", "101");
        
        //Map for scaling factor conversion
        Map Scale = new HashMap();
        Scale.put("1", "00");
        Scale.put("2", "01");
        Scale.put("4", "10");
        Scale.put("8", "11");
        
        //PROGRAM STARTS
        System.out.println("Conversion of Assembly code to hexadecimal:");
        Scanner in = new Scanner(System.in);
        System.out.println("\nWhich mode You are operating in?");
        int mode =in.nextInt();
        if(mode==16)
        {
            mode_16=1;
            W=1;
        }
        else if(mode==32)
        {
            mode_32=1;
            W=1;
        }
        Scanner in1 = new Scanner(System.in);
        System.out.println("\nEnter the Assembly Instruction:");
        String instr = in1.nextLine();
        //extracting the intruction
        String ins = instr.substring(0,3);
        
        //finding if mov is normal, immediate data is available or if data segments are used.
        int comma_index = instr.indexOf(",");                   //position of comma
        check_char1 = instr.substring(comma_index+2);           //source
        char check_char2 = instr.charAt(comma_index+2);         //source's first character
        boolean check_digit1 =isDigit(check_char2);             //checking if data is immediate
        String check_char3 = instr.substring(comma_index+3, comma_index+4);
        if(check_digit1==true)
        {
             mov_opcode="1100011";                     //opcode for MOV when data is immediate
             D_done=1;
        }
        else if(check_char3.compareTo("S")==0)
        {
             mov_opcode="100011";                      //opcode for MOV when data segments are used
        }
        else
        {
             mov_opcode="100010";                      //opcode for MOV when general case occurs
        }
        //Checking direction
        int check_bracket=instr.indexOf("[");          //checking positions of brackets
        int check_bracket_close=instr.indexOf("]");
        int check_mul=instr.indexOf("*");              //checking if scaling factor exists or not
        String check_char4=instr.substring(4,5);       //checking fourth character
        
        if(check_bracket!=-1 && check_bracket<comma_index && D_done==0)//if destination is memory address, direction bit is zero
        {
            D=0;
        }
        else
        {
            if(D_done==0)                                              //if immediate data is present ,no need to assign D explicitly, there D_done becomes 1
            {            
                D=1;
            }
        }
        
        //Calculation of MOD
        int check_plus=instr.indexOf("+");
        if(check_plus!=-1)
        {
             check_plus1 = instr.lastIndexOf("+");
        }
      
        //Immediate data handling
        String comma_digit=instr.substring(comma_index+2, comma_index+3);       //checking if digit is occurring after comma
        if(comma_digit.compareTo("0")==0 || comma_digit.compareTo("1")==0)
        {
            immediate=1;
            if(check_bracket==-1)
            {
                MOD ="11";
                RM="000";
                desti=instr.substring(4, comma_index);
                //search in normal register tables
                for (Iterator it = Register8.keySet().iterator(); it.hasNext() && desti_found==0;) 
                {
                    String key = (String) it.next();
                    if(desti.compareTo(key)==0)
                    {
                        REG=(String)Register8.get(key);
                        desti_found=1;
                        data_l=instr.substring(comma_index+2, comma_index+4);
                        W=0;
                    }
                }
                for (Iterator it = Register16.keySet().iterator(); it.hasNext() && desti_found==0;) 
                {
                    String key = (String) it.next();
                    if(desti.compareTo(key)==0)
                    {
                        REG=(String)Register16.get(key);
                        desti_found=1;
                        if(mode_32==1)
                        {
                            prefix_reg="66";
                        }
                         data_h=instr.substring(comma_index+2, comma_index+4);
                         data_l=instr.substring(comma_index+4);
                         W=1;
                    }
                }
                for (Iterator it = Register32.keySet().iterator(); it.hasNext() && desti_found==0;) 
                {
                    String key = (String) it.next();
                    if(desti.compareTo(key)==0)
                    {
                        REG=(String)Register32.get(key);
                        desti_found=1;
                        if(mode_16==1)
                        {
                            prefix_reg="66";
                        }
                        W=1;
                    }
                }
                //search in segment register table
                for (Iterator it = SegmentReg.keySet().iterator(); it.hasNext() && desti_found==0;) 
                {
                    String key = (String) it.next();
                    if(desti.compareTo(key)==0)
                    {
                        REG=(String)SegmentReg.get(key);
                        desti_found=1;
                        W=1;
                    }
                }
                bin = mov_opcode + D + W + MOD + REG + RM;
                System.out.println("\nbin::"+bin);
                if(data_h!="null" && data_l!="null")
                {
                    data_h=instr.substring(comma_index+2, comma_index+4);
                    data_l=instr.substring(comma_index+4);
                }
                else
                {
                    data_l=instr.substring(comma_index+2, comma_index+4);
                }
            }
            else//Check_bracket!=-1, i.e if bracket is present
            {
                String check_ptr=instr.substring(4, check_bracket);
                if(check_ptr.compareTo("BYTE_PTR")==0){data_bits=8;}
                if(check_ptr.compareTo("WORD_PTR")==0){ data_bits=16;W=1;}
                if(check_ptr.compareTo("DWORD_PTR")==0){ data_bits=32;W=1;}
                if(data_bits!=0)
                {
                    REG="000";
                    //MOD and RM calculation
                    if(check_plus!=-1)
                    {
                        String TestDigit3_1 = instr.substring(check_plus+3, check_plus+4);
                        String TestDigit3_2 = instr.substring(check_plus1+3, check_plus1+4);
                        String TestDigit2_1 = instr.substring(check_plus+2, check_plus+3);
                        String TestDigit2_2 = instr.substring(check_plus1+2, check_plus1+3);
                        String TestDigit_bracket=instr.substring(check_bracket+1,check_bracket+2);
                        if(TestDigit3_1.compareTo("1")==0 || TestDigit3_1.compareTo("0")==0 || TestDigit3_2.compareTo("1")==0 || TestDigit3_2.compareTo("0")==0)
                        {
                            MOD="10";                                    //16-bit displacement
                            desti_sp=1;                                  //desti lies in special mode(coz source is immediate data, therefor sourc cannot be in any special addressing modes)
                            source=instr.substring(comma_index+2);
                            if(check_plus1!=check_plus)
                            {
                                 desti=instr.substring(check_bracket, check_plus1);
                            }
                            else
                            {
                                desti=instr.substring(check_bracket, check_plus);
                            }
                            //claculating RM
                            if(check_mul!=-1)//scaled
                            {
                                if(mode_16==1)
                                {
                                    prefix_reg="66";
                                }
                                RM="100";
                                if(check_plus1!=check_plus)
                                {
                                    index_str=instr.substring(check_mul+1, check_plus1);
                                }
                                else
                                {
                                    index_str=instr.substring(check_mul+1, check_bracket_close);
                                }
                                base_str=instr.substring(check_bracket+1, check_bracket+4);
                                scale_str=instr.substring(check_plus+1, check_mul);
                                for (Iterator it = Register32.keySet().iterator(); it.hasNext();) 
                                {
                                    String key = (String) it.next();
                                    if(index_str.compareTo(key)==0)
                                    {
                                        index_code=(String)Register32.get(key);
                                    }
                                    if(base_str.compareTo(key)==0)
                                    {
                                        base_code=(String)Register32.get(key);
                                    }
                                }
                                for (Iterator it = Scale.keySet().iterator(); it.hasNext();) 
                                {
                                    String key = (String) it.next();
                                    if(scale_str.compareTo(key)==0)
                                    {
                                        scale_code=(String)Scale.get(key);
                                    }
                                }
                                System.out.println("\n scale:"+scale_code);
                                System.out.println("\n base:"+base_code);
                                data_h=instr.substring(comma_index+2, comma_index+4);
                                data_l=instr.substring(comma_index+4);
                                addr_h=instr.substring(check_plus1+1, check_plus1+3);
                                addr_l=instr.substring(check_plus1+3, check_plus1+5);
                                bin=mov_opcode + D + W + MOD + REG + RM + scale_code + index_code + base_code;
                                System.out.println("bin:"+bin);
                                
                            }
                            else//no scaled index
                            {
                                 for (Iterator it = SpecialMode1.keySet().iterator(); it.hasNext() && desti_found==0;)//search in 16 bit addr mode table 
                                 {
                                     String key = (String) it.next();
                                     if(desti.compareTo(key)==0)
                                     {
                                         RM=(String)SpecialMode1.get(key);
                                         desti_found=1;
                                     }
                                 }
                                 for (Iterator it = SpecialMode2.keySet().iterator(); it.hasNext()&& desti_found==0;)//search in 32-bit addr mode table
                                 {
                                     String key = (String) it.next();
                                     if(desti.compareTo(key)==0)
                                     {
                                         RM=(String)SpecialMode2.get(key);
                                         desti_found=1;
                                        if(mode_32==1)
                                        {
                                            prefix_addr="67";
                                        }
                                     }
                                 }
                            }
                            data_h=instr.substring(comma_index+2, comma_index+4);
                            data_l=instr.substring(comma_index+4);
                            addr_h=instr.substring(check_plus1+1, check_plus1+3);
                            addr_l=instr.substring(check_plus1+3, check_plus1+5);
                            bin=mov_opcode + D + W + MOD + REG + RM;
                            System.out.println("\nbin:"+ bin);
                        }
                        //8 bit displacement
                        else if(TestDigit2_1.compareTo("1")==0 || TestDigit2_1.compareTo("0")==0 || TestDigit2_2.compareTo("1")==0 || TestDigit2_2.compareTo("0")==0)
                        {
                            MOD="01";                                    //8-bit displacement
                            desti_sp=1;                                   //desti lies in special mode
                            source=instr.substring(comma_index+2);
                            if(check_plus1!=check_plus)
                            {
                                 desti=instr.substring(4, check_plus1);
                            }
                            else
                            {
                                desti=instr.substring(4, check_plus);
                            }
                            //claculating RM
                            for (Iterator it = SpecialMode1.keySet().iterator(); it.hasNext() && desti_found==0;) //search in 16 bit addr mode table 
                            {
                                String key = (String) it.next();
                                if(desti.compareTo(key)==0)
                                {
                                    RM=(String)SpecialMode1.get(key);
                                    desti_found=1;
                                    if(mode_32==1)
                                    {
                                        prefix_addr="67";
                                    }
                                }
                            }
                            for (Iterator it = SpecialMode2.keySet().iterator(); it.hasNext()&& desti_found==0;)  //search in 32-bit addr mode table
                            {
                                String key = (String) it.next();
                                if(desti.compareTo(key)==0)
                                {
                                    RM=(String)SpecialMode2.get(key);
                                    desti_found=1;
                                    if(mode_16==1)
                                    {
                                        prefix_addr="67";
                                    }
                                }
                            }
                            data_h=instr.substring(comma_index+2, comma_index+4);
                            data_l=instr.substring(comma_index+4);
                            addr_l=instr.substring(check_plus1+3, check_plus1+5);
                            bin=mov_opcode + D + W + MOD + REG + RM;
                            System.out.println("\nbin:"+bin);
                        }
                    }
                    else if(check_plus==-1)
                    {
                        MOD="00";
                        if(check_bracket!=-1)
                        {
                            //handling exception of BP
                            String exBP = instr.substring(check_bracket, check_bracket+3);
                            if((exBP.compareTo("[BP")==0)&&(check_plus==-1))
                            {
                                bp_exc=1;
                                MOD="01";
                                RM="110";
                            }
                        }
                        if(bp_exc!=1)
                        {
                            for (Iterator it = SpecialMode1.keySet().iterator(); it.hasNext() && desti_found==0;)  //search in 16 bit addr mode table
                            {
                                String key = (String) it.next();
                                if(desti.compareTo(key)==0)
                                {
                                    RM=(String)SpecialMode1.get(key);
                                    desti_found=1;
                                     if(mode_32==1)
                                    {
                                        prefix_addr="67";
                                    }
                                }
                            }
                            for (Iterator it = SpecialMode2.keySet().iterator(); it.hasNext()&& desti_found==0;) //search in 32-bit addr mode table
                            {
                                String key = (String) it.next();
                                if(desti.compareTo(key)==0)
                                {
                                    RM=(String)SpecialMode2.get(key);
                                    desti_found=1;
                                     if(mode_16==1)
                                    {
                                        prefix_addr="67";
                                    }
                                }
                            }
                        }
                      
                        data_h=instr.substring(comma_index+2, comma_index+4);
                        data_l=instr.substring(comma_index+4);
                        bin=mov_opcode + D + W + MOD + REG + RM;
                        System.out.println("\nbin:"+bin);
                    }
                }
            }
        }
//immediate data ends
//when immediate is not provided
        if((check_plus==-1)&&(check_bracket==-1)&&(immediate==0))
        {
            MOD = "11";                                     //R/M register 
            source = instr.substring(comma_index+2);        //source
            desti = instr.substring(4, comma_index);        //destination
            //symbolic memory
            special=1;
            source_spCase=1;
            desti_spCase=1;
            check_specialCase_source=instr.substring(comma_index+2);
            for (Iterator it = Register8.keySet().iterator(); it.hasNext();) 
            {
                String key = (String) it.next();
                if(check_specialCase_source.compareTo(key)==0)
                {
                    special=0;
                    source_spCase=0;
                    W=0;
                }
            }
            for (Iterator it = Register16.keySet().iterator(); it.hasNext();) 
            {
                String key = (String) it.next();
                if(check_specialCase_source.compareTo(key)==0)
                {
                    special=0;
                    source_spCase=0;
                    if(mode_32==1)
                    {
                        prefix_reg="66";
                    }
                }
            }
            for (Iterator it = Register32.keySet().iterator(); it.hasNext();) 
            {
                String key = (String) it.next();
                if(check_specialCase_source.compareTo(key)==0)
                {
                    special=0;
                    source_spCase=0;
                    if(mode_16==1)
                    {
                        prefix_reg="66";
                    }
                }
            }
            if(special==0)
            {
                special=1;
                check_specialCase_source=instr.substring(4, comma_index);
                for (Iterator it = Register8.keySet().iterator(); it.hasNext();) 
                {
                    String key = (String) it.next();
                    if(check_specialCase_source.compareTo(key)==0)
                    {
                        special=0;
                        desti_spCase=0;
                        W=0;
                    }
                }
                for (Iterator it = Register16.keySet().iterator(); it.hasNext();) 
                {
                    String key = (String) it.next();
                    if(check_specialCase_source.compareTo(key)==0)
                    {
                        special=0;
                        desti_spCase=0;
                        if(mode_32==1)
                        {
                            prefix_reg="66";
                        }
                    }
                }
                for (Iterator it = Register32.keySet().iterator(); it.hasNext();) 
                {
                    String key = (String) it.next();
                    if(check_specialCase_source.compareTo(key)==0)
                    {
                        special=0;
                        desti_spCase=0;
                        if(mode_16==1)
                        {
                            prefix_reg="66";
                        }
                    }
                }
            }
            if(special==1)
                {
                    MOD="00";
                    RM="110";
                    if(source_spCase==1)
                    {
                        desti=instr.substring(4, comma_index);
                         //search in normal register tables
                        for (Iterator it = Register8.keySet().iterator(); it.hasNext() && desti_found==0;) 
                        {
                            String key = (String) it.next();
                            if(desti.compareTo(key)==0)
                            {
                                REG=(String)Register8.get(key);
                                desti_found=1;
                                W=0;
                            }
                        }
                        for (Iterator it = Register16.keySet().iterator(); it.hasNext() && desti_found==0;) 
                        {
                            String key = (String) it.next();
                            if(desti.compareTo(key)==0)
                            {
                                REG=(String)Register16.get(key);
                                desti_found=1;
                                if(mode_32==1)
                                {
                                    prefix_reg="66";
                                }
                            }
                        }
                        for (Iterator it = Register32.keySet().iterator(); it.hasNext() && desti_found==0;) 
                        {
                            String key = (String) it.next();
                            if(desti.compareTo(key)==0)
                            {
                                REG=(String)Register32.get(key);
                                desti_found=1;
                                 if(mode_16==1)
                                {
                                    prefix_reg="66";
                                }
                            }
                        }
                        for (Iterator it = SegmentReg.keySet().iterator(); it.hasNext() && desti_found==0;) 
                        {
                            String key = (String) it.next();
                            if(desti.compareTo(key)==0)
                            {
                                REG=(String)SegmentReg.get(key);
                                desti_found=1;
                            }
                        }
                    }
                    else if(desti_spCase==1)
                    {
                        D=0;
                        source=instr.substring(comma_index+2);
                        for (Iterator it = Register8.keySet().iterator(); it.hasNext() && source_found==0;) 
                        {
                            String key = (String) it.next();
                            if(source.compareTo(key)==0)
                            {
                                REG=(String)Register8.get(key);
                                source_found=1;
                                W=0;
                            }
                        }
                        for (Iterator it = Register16.keySet().iterator(); it.hasNext() && source_found==0;) 
                        {
                            String key = (String) it.next();
                            if(source.compareTo(key)==0)
                            {
                                REG=(String)Register16.get(key);
                                source_found=1;
                                 if(mode_32==1)
                                {
                                    prefix_reg="66";
                                }
                            }
                        }
                        for (Iterator it = Register32.keySet().iterator(); it.hasNext() && source_found==0;) 
                        {
                            String key = (String) it.next();
                            if(source.compareTo(key)==0)
                            {
                                REG=(String)Register32.get(key);
                                source_found=1;
                                if(mode_16==1)
                               {
                                   prefix_reg="66";
                               }
                            }

                        }
                        for (Iterator it = SegmentReg.keySet().iterator(); it.hasNext() && source_found==0;) 
                        {
                            String key = (String) it.next();
                            if(source.compareTo(key)==0)
                            {
                                REG=(String)SegmentReg.get(key);
                                source_found=1;
                            }
                        }
                    }
                     bin=mov_opcode + D + W + MOD + REG + RM;
                     System.out.println("\nbin:"+bin);
                }//
        }
        else if(immediate==0)
        {
            String TestDigit3_1 = instr.substring(check_plus+3, check_plus+4);
            String TestDigit3_2 = instr.substring(check_plus1+3, check_plus1+4);
            String TestDigit2_1 = instr.substring(check_plus+2, check_plus+3);
            String TestDigit2_2 = instr.substring(check_plus1+2, check_plus1+3);
            String TestDigit_bracket=instr.substring(check_bracket+1,check_bracket+2);
            if(check_bracket < comma_index)
            {
                check_blank=instr.substring(3, check_bracket);
            }
            else
            {
                check_blank=instr.substring(comma_index+1, check_bracket);
            }
            if(TestDigit3_1.compareTo("1")==0 || TestDigit3_1.compareTo("0")==0 || TestDigit3_2.compareTo("1")==0 || TestDigit3_2.compareTo("0")==0)
            {
                MOD="10";                                    //16-bit displacement
                data_h=instr.substring(check_plus1+1, check_plus1+3);
                data_l=instr.substring(check_plus1+3,check_plus1+5);
                //scaled//
                    if(check_mul!=-1)
                    {
                         if(mode_16==1)
                        {
                            prefix_addr="67";
                        }
                        scaled=1;
                        RM="100";
                        if(check_plus1!=check_plus)
                        {
                            index_str=instr.substring(check_mul+1, check_plus1);
                        }
                        else
                        {
                            index_str=instr.substring(check_mul+1, check_bracket_close);
                        }
                        base_str=instr.substring(check_bracket+1, check_bracket+4);
                        scale_str=instr.substring(check_plus+1, check_mul);
                        for (Iterator it = Register32.keySet().iterator(); it.hasNext();) 
                        {
                            String key = (String) it.next();
                            if(index_str.compareTo(key)==0)
                            {
                                index_code=(String)Register32.get(key);
                                if(mode_16==1)
                                {
                                    prefix_addr="67";
                                }
                            }
                            if(base_str.compareTo(key)==0)
                            {
                                base_code=(String)Register32.get(key);
                                if(mode_16==1)
                                {
                                    prefix_addr="67";
                                }
                            }
                        }
                        for (Iterator it = Scale.keySet().iterator(); it.hasNext();) 
                        {
                            String key = (String) it.next();
                            if(scale_str.compareTo(key)==0)
                            {
                                scale_code=(String)Scale.get(key);
                            }
                        }
                        System.out.println("\n scale:"+scale_code);
                        System.out.println("\n base:"+base_code);
                        //REG calculation
                        desti=instr.substring(4, comma_index);
                        //search in normal register tables
                        for (Iterator it = Register8.keySet().iterator(); it.hasNext() && desti_found==0;) 
                        {
                            String key = (String) it.next();
                            if(desti.compareTo(key)==0)
                            {
                                REG=(String)Register8.get(key);
                                desti_found=1;
                                W=0;
                            }
                        }
                         for (Iterator it = Register16.keySet().iterator(); it.hasNext() && desti_found==0;) 
                        {
                            String key = (String) it.next();
                            if(desti.compareTo(key)==0)
                            {
                                REG=(String)Register16.get(key);
                                desti_found=1;
                                 if(mode_32==1)
                                {
                                    prefix_reg="66";
                                }
                            }
                        }
                         for (Iterator it = Register32.keySet().iterator(); it.hasNext() && desti_found==0;) 
                        {
                            String key = (String) it.next();
                            if(desti.compareTo(key)==0)
                            {
                                REG=(String)Register32.get(key);
                                desti_found=1;
                                 if(mode_16==1)
                                {
                                    prefix_reg="66";
                                }
                            }
                        }
                        //search in segment register table
                        for (Iterator it = SegmentReg.keySet().iterator(); it.hasNext() && desti_found==0;) 
                        {
                            String key = (String) it.next();
                            if(desti.compareTo(key)==0)
                            {
                                REG=(String)SegmentReg.get(key);
                                desti_found=1;
                            }
                        }
                        addr_h=instr.substring(check_plus1+1, check_plus1+3);
                        addr_l=instr.substring(check_plus1+3, check_plus1+5); 
                        bin=mov_opcode + D + W + MOD + REG + RM + scale_code + index_code + base_code;
                        System.out.println("\nbin:"+bin);
                    }
                else//not scaled
                {                  
                    if(check_blank.compareTo(" ")==0)
                    {
                        if(check_bracket>comma_index)
                        {
                            source_sp=1;//source lies in special modes
                            desti=instr.substring(4, comma_index);
                            if(check_plus1!=-1)
                            {
                                 source=instr.substring(comma_index+2, check_plus1);
                            }
                            else
                            {
                              source=instr.substring(comma_index+2, check_plus);   
                            }
                        }
                        else
                        {
                            desti_sp=1; //desti lies in special mode
                            source=instr.substring(comma_index+2);
                            if(check_plus1!=-1)
                            {
                                 desti=instr.substring(4, check_plus1);
                            }
                            else
                            {
                                desti=instr.substring(4, check_plus);
                            }
                        }
                    }
                    else
                    {
                    //symbolic memory
                    special=1;
                    source_spCase=1;
                    desti_spCase=1;
                    check_specialCase_source=instr.substring(comma_index+2);
                    for (Iterator it = Register8.keySet().iterator(); it.hasNext();) 
                    {
                        String key = (String) it.next();
                        if(check_specialCase_source.compareTo(key)==0)
                        {
                            special=0;
                            source_spCase=0;
                        }
                    }
                    for (Iterator it = Register16.keySet().iterator(); it.hasNext();) 
                    {
                        String key = (String) it.next();
                        if(check_specialCase_source.compareTo(key)==0)
                        {
                            special=0;
                            source_spCase=0;
                            if(mode_32==1)
                            {
                                prefix_reg="66";
                            }
                        }
                    }
                    for (Iterator it = Register32.keySet().iterator(); it.hasNext();) 
                    {
                        String key = (String) it.next();
                        if(check_specialCase_source.compareTo(key)==0)
                        {
                            special=0;
                            source_spCase=0;
                            if(mode_16==1)
                            {
                                prefix_reg="66";
                            }
                        }
                    }
                    if(special==0)
                    {
                        special=1;
                        check_specialCase_source=instr.substring(4, comma_index);
                        for (Iterator it = Register8.keySet().iterator(); it.hasNext();) 
                        {
                            String key = (String) it.next();
                            if(check_specialCase_source.compareTo(key)==0)
                            {
                                special=0;
                                desti_spCase=0;
                                W=0;
                            }
                        }
                        for (Iterator it = Register16.keySet().iterator(); it.hasNext();) 
                        {
                            String key = (String) it.next();
                            if(check_specialCase_source.compareTo(key)==0)
                            {
                                special=0;
                                desti_spCase=0;
                                if(mode_32==1)
                                {
                                    prefix_reg="66";
                                }
                            }
                        }
                        for (Iterator it = Register32.keySet().iterator(); it.hasNext();) 
                        {
                            String key = (String) it.next();
                            if(check_specialCase_source.compareTo(key)==0)
                            {
                                special=0;
                                desti_spCase=0;
                                if(mode_16==1)
                                {
                                    prefix_reg="66";
                                }
                            }
                        }
                    }
                     if(special==1)
                        {
                            MOD="00";
                            RM="110";
                            if(source_spCase==1)
                            {
                                desti=instr.substring(4, comma_index);
                                //search in normal register table
                                for (Iterator it = Register8.keySet().iterator(); it.hasNext() && desti_found==0;) 
                                {
                                    String key = (String) it.next();
                                    if(desti.compareTo(key)==0)
                                    {
                                        REG=(String)Register8.get(key);
                                        desti_found=1;
                                        W=0;
                                    }
                                }
                                for (Iterator it = Register16.keySet().iterator(); it.hasNext() && desti_found==0;) 
                                {
                                    String key = (String) it.next();
                                    if(desti.compareTo(key)==0)
                                    {
                                        REG=(String)Register16.get(key);
                                        desti_found=1;
                                        if(mode_32==1)
                                        {
                                            prefix_reg="66";
                                        }
                                    }
                                }
                                for (Iterator it = Register32.keySet().iterator(); it.hasNext() && desti_found==0;) 
                                {
                                    String key = (String) it.next();
                                    if(desti.compareTo(key)==0)
                                    {
                                        REG=(String)Register32.get(key);
                                        desti_found=1;
                                         if(mode_16==1)
                                        {
                                            prefix_reg="66";
                                        }
                                    }
                                }
                                //search in segment register table
                                for (Iterator it = SegmentReg.keySet().iterator(); it.hasNext() && desti_found==0;) 
                                {
                                    String key = (String) it.next();
                                    if(desti.compareTo(key)==0)
                                    {
                                        REG=(String)SegmentReg.get(key);
                                        desti_found=1;
                                    }
                                }
                            }
                            else if(desti_spCase==1)
                            {
                                D=0;
                                source=instr.substring(comma_index+2);
                                for (Iterator it = Register8.keySet().iterator(); it.hasNext() && source_found==0;) 
                                {
                                    String key = (String) it.next();
                                    if(source.compareTo(key)==0)
                                    {
                                        REG=(String)Register8.get(key);
                                        source_found=1;
                                        W=0;
                                    }
                                }
                                for (Iterator it = Register16.keySet().iterator(); it.hasNext() && source_found==0;) 
                                {
                                    String key = (String) it.next();
                                    if(source.compareTo(key)==0)
                                    {
                                        REG=(String)Register16.get(key);
                                        source_found=1;
                                         if(mode_32==1)
                                        {
                                            prefix_reg="66";
                                        }
                                         W=1;
                                    }
                                }
                                for (Iterator it = Register32.keySet().iterator(); it.hasNext() && source_found==0;) 
                                {
                                    String key = (String) it.next();
                                    if(source.compareTo(key)==0)
                                    {
                                        REG=(String)Register32.get(key);
                                        source_found=1;
                                        W=1;
                                        if(mode_16==1)
                                       {
                                           prefix_reg="66";
                                       }
                                    }
                                }
                                for (Iterator it = SegmentReg.keySet().iterator(); it.hasNext() && source_found==0;) 
                                {
                                    String key = (String) it.next();
                                    if(source.compareTo(key)==0)
                                    {
                                        REG=(String)SegmentReg.get(key);
                                        source_found=1;
                                        W=1;
                                    }
                                }
                            }
                             bin=mov_opcode + D + W + MOD + REG + RM;
                             System.out.println("\nbin:"+bin);
                        }//
                    }
                }
            }
            else if(TestDigit2_1.compareTo("1")==0 || TestDigit2_1.compareTo("0")==0 || TestDigit2_2.compareTo("1")==0 || TestDigit2_2.compareTo("0")==0)
            {
                MOD="01";                                    //8-bit displacement
                data_l=instr.substring(check_plus1+1, check_plus1+3);
                    if(check_blank.compareTo(" ")==0)
                    {
                        if(check_bracket>4)
                        {
                            source_sp=1;//source lies in special modes
                            desti=instr.substring(4, comma_index);
                            if(check_plus1!=-1)
                            {
                                 source=instr.substring(comma_index+2, check_plus1);
                            }
                            else
                            {
                              source=instr.substring(comma_index+2, check_plus);   
                            }
                        }
                        else
                        {
                            desti_sp=1; //desti lies in special mode
                            source=instr.substring(comma_index+2);
                            if(check_plus1!=-1)
                            {
                                 desti=instr.substring(4, check_plus1);
                            }
                            else
                            {
                                desti=instr.substring(4, check_plus);
                            }
                        }///
                    }
                    else
                    {
                        //symbolic memory
                        special=1;
                        source_spCase=1;
                        desti_spCase=1;
                        check_specialCase_source=instr.substring(comma_index+2);
                        for (Iterator it = Register8.keySet().iterator(); it.hasNext();) 
                        {
                            String key = (String) it.next();
                            if(check_specialCase_source.compareTo(key)==0)
                            {
                                special=0;
                                source_spCase=0;
                                W=0;
                            }
                        }
                        for (Iterator it = Register16.keySet().iterator(); it.hasNext();) 
                        {
                            String key = (String) it.next();
                            if(check_specialCase_source.compareTo(key)==0)
                            {
                                special=0;
                                source_spCase=0;
                                if(mode_32==1)
                                {
                                    prefix_reg="66";
                                }
                                W=1;
                            }
                        }
                        for (Iterator it = Register32.keySet().iterator(); it.hasNext();) 
                        {
                            String key = (String) it.next();
                            if(check_specialCase_source.compareTo(key)==0)
                            {
                                special=0;
                                source_spCase=0;
                                if(mode_16==1)
                                {
                                    prefix_reg="66";
                                }
                                W=1;
                            }
                        }
                        if(special==0)
                        {
                            special=1;
                            check_specialCase_source=instr.substring(4, 8);
                            for (Iterator it = Register8.keySet().iterator(); it.hasNext();) 
                            {
                                String key = (String) it.next();
                                if(check_specialCase_source.compareTo(key)==0)
                                {
                                    special=0;
                                    desti_spCase=0;
                                }
                            }
                             for (Iterator it = Register16.keySet().iterator(); it.hasNext();) 
                            {
                                String key = (String) it.next();
                                if(check_specialCase_source.compareTo(key)==0)
                                {
                                    special=0;
                                    desti_spCase=0;
                                    if(mode_32==1)
                                    {
                                        prefix_reg="66";
                                    }
                                }
                            }
                             for (Iterator it = Register32.keySet().iterator(); it.hasNext();) 
                            {
                                String key = (String) it.next();
                                if(check_specialCase_source.compareTo(key)==0)
                                {
                                    special=0;
                                    desti_spCase=0;
                                }
                                if(mode_16==1)
                                {
                                    prefix_reg="66";
                                }
                            }
                        }
                        if(special==1)
                            {
                                MOD="00";
                                RM="110";
                                if(source_spCase==1)
                                {
                                    desti=instr.substring(4, comma_index);
                                     //search in normal register table
                                    for (Iterator it = Register8.keySet().iterator(); it.hasNext() && desti_found==0;) 
                                    {
                                        String key = (String) it.next();
                                        if(desti.compareTo(key)==0)
                                        {
                                            REG=(String)Register8.get(key);
                                            desti_found=1;
                                            W=0;
                                        }
                                    }
                                    for (Iterator it = Register16.keySet().iterator(); it.hasNext() && desti_found==0;) 
                                    {
                                        String key = (String) it.next();
                                        if(desti.compareTo(key)==0)
                                        {
                                            REG=(String)Register16.get(key);
                                            desti_found=1;
                                            if(mode_32==1)
                                            {
                                                prefix_reg="66";
                                            }
                                            W=1;
                                        }
                                    }
                                    for (Iterator it = Register32.keySet().iterator(); it.hasNext() && desti_found==0;) 
                                    {
                                        String key = (String) it.next();
                                        if(desti.compareTo(key)==0)
                                        {
                                            REG=(String)Register32.get(key);
                                            desti_found=1;
                                            if(mode_16==1)
                                            {
                                                prefix_reg="66";
                                            }
                                            W=1;
                                        }
                                    }
                                    //search in segment register table
                                    for (Iterator it = SegmentReg.keySet().iterator(); it.hasNext() && desti_found==0;) 
                                    {
                                        String key = (String) it.next();
                                        if(desti.compareTo(key)==0)
                                        {
                                            REG=(String)SegmentReg.get(key);
                                            desti_found=1;
                                            W=1;
                                        }
                                    }
                                }
                                else if(desti_spCase==1)
                                {
                                    D=0;
                                    source=instr.substring(comma_index+2);
                                    for (Iterator it = Register8.keySet().iterator(); it.hasNext() && source_found==0;) 
                                    {
                                        String key = (String) it.next();
                                        if(source.compareTo(key)==0)
                                        {
                                            REG=(String)Register8.get(key);
                                            source_found=1;
                                            W=0;
                                        }
                                    }
                                    for (Iterator it = Register16.keySet().iterator(); it.hasNext() && source_found==0;) 
                                    {
                                        String key = (String) it.next();
                                        if(source.compareTo(key)==0)
                                        {
                                            REG=(String)Register16.get(key);
                                            source_found=1;
                                            if(mode_32==1)
                                            {
                                                prefix_reg="66";
                                            }
                                            W=1;
                                        }
                                    }
                                    for (Iterator it = Register32.keySet().iterator(); it.hasNext() && source_found==0;) 
                                    {
                                        String key = (String) it.next();
                                        if(source.compareTo(key)==0)
                                        {
                                            REG=(String)Register32.get(key);
                                            W=1;
                                            source_found=1;
                                            if(mode_16==1)
                                            {
                                                prefix_reg="66";
                                            }
                                        }
                                    }
                                    for (Iterator it = SegmentReg.keySet().iterator(); it.hasNext() && source_found==0;) 
                                    {
                                        String key = (String) it.next();
                                        if(source.compareTo(key)==0)
                                        {
                                            REG=(String)SegmentReg.get(key);
                                            source_found=1;
                                            W=1;
                                        }
                                    }
                                }
                                addr_l=instr.substring(check_plus1+3, check_plus1+5);
                                bin=mov_opcode + D + W + MOD + REG + RM;
                                System.out.println("\nbin:"+bin);
                            }//
                    }
            }
            else
            {
                MOD="00";                                    //No Displacement  
                //scaled//
                    if(check_mul!=-1)
                    {
                        scaled=1;
                        RM="100";
                        if(check_plus1!=check_plus)
                        {
                            index_str=instr.substring(check_mul+1, check_plus1);
                        }
                        else
                        {
                            index_str=instr.substring(check_mul+1, check_bracket_close);
                        }
                        base_str=instr.substring(check_bracket+1, check_bracket+4);
                        scale_str=instr.substring(check_plus+1, check_mul);
                        for (Iterator it = Register32.keySet().iterator(); it.hasNext();) 
                        {
                            String key = (String) it.next();
                            if(index_str.compareTo(key)==0)
                            {
                                index_code=(String)Register32.get(key);
                                if(mode_16==1)
                                {
                                    prefix_addr="67";
                                }

                            }
                            if(base_str.compareTo(key)==0)
                            {
                                base_code=(String)Register32.get(key);
                                if(mode_16==1)
                                {
                                    prefix_addr="67";
                                }
                            }
                        }
                        for (Iterator it = Scale.keySet().iterator(); it.hasNext();) 
                        {
                            String key = (String) it.next();
                            if(scale_str.compareTo(key)==0)
                            {
                                scale_code=(String)Scale.get(key);
                            }
                        }
                        System.out.println("\n scale:"+scale_code);
                        System.out.println("\n base:"+base_code);
                        //reg calculation
                        desti=instr.substring(4, comma_index);
                        //search in normal register table
                        for (Iterator it = Register8.keySet().iterator(); it.hasNext() && desti_found==0;) 
                        {
                            String key = (String) it.next();
                            if(desti.compareTo(key)==0)
                            {
                                REG=(String)Register8.get(key);
                                desti_found=1;
                                W=0;
                            }
                        }
                         for (Iterator it = Register16.keySet().iterator(); it.hasNext() && desti_found==0;) 
                        {
                            String key = (String) it.next();
                            if(desti.compareTo(key)==0)
                            {
                                REG=(String)Register16.get(key);
                                desti_found=1;
                                if(mode_32==1)
                                {
                                    prefix_reg="66";
                                }
                            }
                        }
                        for (Iterator it = Register32.keySet().iterator(); it.hasNext() && desti_found==0;) 
                        {
                            String key = (String) it.next();
                            if(desti.compareTo(key)==0)
                            {
                                REG=(String)Register32.get(key);
                                desti_found=1;
                                if(mode_16==1)
                                {
                                    prefix_reg="66";
                                }
                            }
                        }
                        //search in segment register table
                        for (Iterator it = SegmentReg.keySet().iterator(); it.hasNext() && desti_found==0;) 
                        {
                            String key = (String) it.next();
                            if(desti.compareTo(key)==0)
                            {
                                REG=(String)SegmentReg.get(key);
                                desti_found=1;
                            }
                        }
                        bin=mov_opcode + D + W + MOD + REG + RM + scale_code + index_code + base_code;
                        System.out.println("\nbin:"+bin);
                    }
                //scled//
                else
                {
                    if(check_bracket!=-1)
                    { //handling exception of BP
                        String exBP = instr.substring(check_bracket, check_bracket+3);
                        if((exBP.compareTo("[BP")==0)&&(check_plus==-1))
                        {
                            bp_exc=1;
                            if(mode_32==1)
                            {
                                prefix_addr="67";
                            }
                            MOD="01";
                            RM="110";
                            System.out.println("\nBP EXCEPTION");
                            //checking weather other operand than BP is source or destination
                            if(check_bracket>4)
                            {
                                desti=instr.substring(4,comma_index);
                                source_sp=1;
                                //search in normal register table
                                for (Iterator it = Register8.keySet().iterator(); it.hasNext() && desti_found==0;) 
                                {
                                    String key = (String) it.next();
                                    if(desti.compareTo(key)==0)
                                    {
                                        REG=(String)Register8.get(key);
                                        desti_found=1;
                                        W=0;
                                    }
                                }
                                for (Iterator it = Register16.keySet().iterator(); it.hasNext() && desti_found==0;) 
                                {
                                    String key = (String) it.next();
                                    if(desti.compareTo(key)==0)
                                    {
                                        REG=(String)Register16.get(key);
                                        desti_found=1;
                                        if(mode_32==1)
                                        {
                                            prefix_reg="66";
                                        }
                                        W=1;
                                    }
                                }
                                for (Iterator it = Register32.keySet().iterator(); it.hasNext() && desti_found==0;) 
                                {
                                    String key = (String) it.next();
                                    if(desti.compareTo(key)==0)
                                    {
                                        REG=(String)Register32.get(key);
                                        desti_found=1;
                                        if(mode_32==1)
                                        {
                                            prefix_reg="66";
                                        }
                                        W=1;
                                    }
                                }
                                //search in segment register table
                                for (Iterator it = SegmentReg.keySet().iterator(); it.hasNext() && desti_found==0;) 
                                {
                                    String key = (String) it.next();
                                    if(desti.compareTo(key)==0)
                                    {
                                        REG=(String)SegmentReg.get(key);
                                        desti_found=1;
                                        W=1;
                                    }
                                }
                            } 
                            else if(check_bracket==4)
                            {
                                source=instr.substring(comma_index+2);
                                desti_sp=1;
                                for (Iterator it = Register8.keySet().iterator(); it.hasNext() && source_found==0;) 
                                {
                                    String key = (String) it.next();
                                    if(source.compareTo(key)==0)
                                    {
                                        REG=(String)Register8.get(key);
                                        source_found=1;
                                        W=0;
                                    }
                                }
                                 for (Iterator it = Register16.keySet().iterator(); it.hasNext() && source_found==0;) 
                                {
                                    String key = (String) it.next();
                                    if(source.compareTo(key)==0)
                                    {
                                        REG=(String)Register16.get(key);
                                        source_found=1;
                                        if(mode_32==1)
                                        {
                                            prefix_reg="66";
                                        }
                                        W=1;
                                    }
                                }
                                  for (Iterator it = Register32.keySet().iterator(); it.hasNext() && source_found==0;) 
                                {
                                    String key = (String) it.next();
                                    if(source.compareTo(key)==0)
                                    {
                                        REG=(String)Register32.get(key);
                                        source_found=1;
                                        if(mode_16==1)
                                        {
                                            prefix_reg="66";
                                        }
                                        W=1;
                                    }
                                }
                                for (Iterator it = SegmentReg.keySet().iterator(); it.hasNext() && source_found==0;) 
                                {
                                    String key = (String) it.next();
                                    if(source.compareTo(key)==0)
                                    {
                                        REG=(String)SegmentReg.get(key);
                                        source_found=1;
                                        W=1;
                                    }
                                }
                            }
                            bin=mov_opcode + D + W + MOD + REG + RM;
                            System.out.println("\nbin:"+bin);
                        }
                        else
                        {
                            if(check_bracket>4)
                            {
                                //Having direct address in brackets
                                if(TestDigit_bracket.compareTo("0")==0 || TestDigit_bracket.compareTo("1")==0)
                                {
                                     special=1;
                                     source_spCase=1;
                                }
                                else
                                {
                                    desti=instr.substring(4, comma_index);
                                    source_sp=1;
                                    if(check_plus1!=check_plus)
                                    {
                                        source=instr.substring(check_bracket,check_plus1);
                                    }
                                    else
                                    {
                                        String check_digit=instr.substring(check_plus+1,check_plus+2);
                                        if(check_digit.compareTo("0")==0 || check_digit.compareTo("1")==0)
                                        {
                                            source=instr.substring(check_bracket,check_plus);
                                        }
                                        else
                                        {

                                            source=instr.substring(check_bracket, check_bracket_close);
                                        }
                                    }
                                }
                            }
                            else if(check_bracket==4)
                            {
                                if(TestDigit_bracket.compareTo("0")==0 || TestDigit_bracket.compareTo("1")==0)
                                {
                                    special=1;
                                    desti_spCase=1;
                                }
                                else
                                {
                                    source=instr.substring(comma_index+2);
                                    desti_sp=1;
                                    //System.out.println("\ncheck_plus1::"+check_plus1);
                                    if(check_plus1!=check_plus)
                                    {
                                        desti=instr.substring(check_bracket,check_plus1);
                                        //System.out.println("\nit should not cm here 1!");
                                    }
                                    else
                                    {
                                        String check_digit=instr.substring(check_plus+1,check_plus+2);
                                        if(check_digit.compareTo("0")==0 || check_digit.compareTo("1")==0)
                                        {
                                            //System.out.println("\nit should not cm here 2!");
                                            desti=instr.substring(check_bracket,check_plus);
                                        }
                                        else
                                        {
                                            desti=instr.substring(check_bracket, check_bracket_close);
                                        }
                                    }
                                }
                            }
                            else
                            {
                                if(check_blank.compareTo(" ")!=0)
                                {
                                    //symbolic memory
                                    special=1;
                                    source_spCase=1;
                                    desti_spCase=1;
                                    check_specialCase_source=instr.substring(comma_index+2);
                                    for (Iterator it = Register8.keySet().iterator(); it.hasNext();) 
                                    {
                                        String key = (String) it.next();
                                        if(check_specialCase_source.compareTo(key)==0)
                                        {
                                            special=0;
                                            source_spCase=0;
                                        }
                                    }
                                    for (Iterator it = Register16.keySet().iterator(); it.hasNext();) 
                                    {
                                        String key = (String) it.next();
                                        if(check_specialCase_source.compareTo(key)==0)
                                        {
                                            special=0;
                                            source_spCase=0;
                                        }
                                    }
                                    for (Iterator it = Register32.keySet().iterator(); it.hasNext();) 
                                    {
                                        String key = (String) it.next();
                                        if(check_specialCase_source.compareTo(key)==0)
                                        {
                                            special=0;
                                            source_spCase=0;
                                        }
                                    }
                                    if(special==0)
                                    {
                                        special=1;
                                        check_specialCase_source=instr.substring(4, 8);
                                        for (Iterator it = Register8.keySet().iterator(); it.hasNext();) 
                                        {
                                            String key = (String) it.next();
                                            if(check_specialCase_source.compareTo(key)==0)
                                            {
                                                special=0;
                                                desti_spCase=0;
                                            }
                                        }
                                        for (Iterator it = Register16.keySet().iterator(); it.hasNext();) 
                                        {
                                            String key = (String) it.next();
                                            if(check_specialCase_source.compareTo(key)==0)
                                            {
                                                special=0;
                                                desti_spCase=0;
                                                if(mode_32==1)
                                                {
                                                    prefix_reg="66";
                                                }
                                            }
                                        }
                                        for (Iterator it = Register32.keySet().iterator(); it.hasNext();) 
                                        {
                                            String key = (String) it.next();
                                            if(check_specialCase_source.compareTo(key)==0)
                                            {
                                                special=0;
                                                desti_spCase=0;
                                                if(mode_32==1)
                                                {
                                                    prefix_reg="66";
                                                }
                                            }
                                        }
                                    }
                                    if(special==1)
                                    {
                                        MOD="00";
                                        RM="110";
                                        if(source_spCase==1)
                                        {
                                            desti=instr.substring(4, comma_index);
                                             //search in normal register table
                                            for (Iterator it = Register8.keySet().iterator(); it.hasNext() && desti_found==0;) 
                                            {
                                                String key = (String) it.next();
                                                if(desti.compareTo(key)==0)
                                                {
                                                    REG=(String)Register8.get(key);
                                                    desti_found=1;
                                                    W=0;
                                                }
                                            }
                                            for (Iterator it = Register16.keySet().iterator(); it.hasNext() && desti_found==0;) 
                                            {
                                                String key = (String) it.next();
                                                if(desti.compareTo(key)==0)
                                                {
                                                    REG=(String)Register16.get(key);
                                                    desti_found=1;
                                                    if(mode_32==1)
                                                    {
                                                        prefix_reg="66";
                                                    }
                                                    W=1;
                                                }
                                            }
                                            for (Iterator it = Register32.keySet().iterator(); it.hasNext() && desti_found==0;) 
                                            {
                                                String key = (String) it.next();
                                                if(desti.compareTo(key)==0)
                                                {
                                                    REG=(String)Register32.get(key);
                                                    desti_found=1;
                                                }
                                                W=1;
                                            }
                                            //search in segment register table
                                            for (Iterator it = SegmentReg.keySet().iterator(); it.hasNext() && desti_found==0;) 
                                            {
                                                String key = (String) it.next();
                                                if(desti.compareTo(key)==0)
                                                {
                                                    REG=(String)SegmentReg.get(key);
                                                    desti_found=1;
                                                    W=1;
                                                }
                                            }
                                        }
                                        else if(desti_spCase==1)
                                        {
                                            D=0;
                                            source=instr.substring(comma_index+2);
                                            for (Iterator it = Register8.keySet().iterator(); it.hasNext() && source_found==0;) 
                                            {
                                                String key = (String) it.next();
                                                if(source.compareTo(key)==0)
                                                {
                                                    REG=(String)Register8.get(key);
                                                    source_found=1;
                                                    W=0;
                                                }
                                            }
                                            for (Iterator it = Register16.keySet().iterator(); it.hasNext() && source_found==0;) 
                                            {
                                                String key = (String) it.next();
                                                if(source.compareTo(key)==0)
                                                {
                                                    REG=(String)Register16.get(key);
                                                    source_found=1;
                                                    if(mode_32==1)
                                                    {
                                                        prefix_reg="66";
                                                    }
                                                    W=1;
                                                }
                                            }
                                            for (Iterator it = Register32.keySet().iterator(); it.hasNext() && source_found==0;) 
                                            {
                                                String key = (String) it.next();
                                                if(source.compareTo(key)==0)
                                                {
                                                    REG=(String)Register32.get(key);
                                                    source_found=1;
                                                    if(mode_32==1)
                                                    {
                                                        prefix_reg="66";
                                                    }
                                                    W=1;
                                                }
                                            }
                                            for (Iterator it = SegmentReg.keySet().iterator(); it.hasNext() && source_found==0;) 
                                            {
                                                String key = (String) it.next();
                                                if(source.compareTo(key)==0)
                                                {
                                                    REG=(String)SegmentReg.get(key);
                                                    source_found=1;
                                                    W=1;
                                                }
                                            }
                                        }
                                        bin=mov_opcode + D + W + MOD + REG + RM;
                                        System.out.println("\nbin:"+bin);
                                    }//
                            }
                        }
                    }
                }
                if(special==1)      
                {
                    MOD="00";
                    RM="110";
                    if(check_bracket>4)
                    {
                        desti=instr.substring(4, comma_index);
                        //search in normal register table
                        for (Iterator it = Register8.keySet().iterator(); it.hasNext() && desti_found==0;) 
                        {
                            String key = (String) it.next();
                            if(desti.compareTo(key)==0)
                            {
                                REG=(String)Register8.get(key);
                                desti_found=1;
                                W=0;
                            }
                        }
                         for (Iterator it = Register16.keySet().iterator(); it.hasNext() && desti_found==0;) 
                        {
                            String key = (String) it.next();
                            if(desti.compareTo(key)==0)
                            {
                                REG=(String)Register16.get(key);
                                desti_found=1;
                                if(mode_32==1)
                                {
                                    prefix_reg="66";
                                }
                                W=1;
                            }
                        }
                        for (Iterator it = Register32.keySet().iterator(); it.hasNext() && desti_found==0;) 
                        {
                            String key = (String) it.next();
                            if(desti.compareTo(key)==0)
                            {
                                REG=(String)Register32.get(key);
                                desti_found=1;
                                if(mode_16==1)
                                {
                                    prefix_reg="66";
                                }
                                W=1;
                            }
                        }
                        //search in segment register table
                        for (Iterator it = SegmentReg.keySet().iterator(); it.hasNext() && desti_found==0;) 
                        {
                            String key = (String) it.next();
                            if(desti.compareTo(key)==0)
                            {
                                REG=(String)SegmentReg.get(key);
                                desti_found=1;
                                W=1;
                            }
                        }
                    }
                    else if(check_bracket==4)
                    {
                        source=instr.substring(comma_index+2);
                        for (Iterator it = Register8.keySet().iterator(); it.hasNext() && source_found==0;) 
                        {
                            String key = (String) it.next();
                            if(source.compareTo(key)==0)
                            {
                                REG=(String)Register8.get(key);
                                source_found=1;
                                W=0;
                            }
                        }
                        for (Iterator it = Register16.keySet().iterator(); it.hasNext() && source_found==0;) 
                        {
                            String key = (String) it.next();
                            if(source.compareTo(key)==0)
                            {
                                REG=(String)Register16.get(key);
                                source_found=1;
                                if(mode_32==1)
                                {
                                    prefix_reg="66";
                                }
                                W=1;
                            }
                        }
                        for (Iterator it = Register32.keySet().iterator(); it.hasNext() && source_found==0;) 
                        {
                            String key = (String) it.next();
                            if(source.compareTo(key)==0)
                            {
                                REG=(String)Register32.get(key);
                                source_found=1;
                                if(mode_16==1)
                                {
                                    prefix_reg="66";
                                }
                                W=1;
                            }
                        }
                        for (Iterator it = SegmentReg.keySet().iterator(); it.hasNext() && source_found==0;) 
                        {
                            String key = (String) it.next();
                            if(source.compareTo(key)==0)
                            {
                                REG=(String)SegmentReg.get(key);
                                source_found=1;
                                W=1;
                            }
                        }
                    }
                     bin= mov_opcode + D + W + MOD + REG + RM;
                     System.out.println("\nbin:"+bin);
                }//
            }
          }
        }
        //assigning binary code to source and desti
        if(special!=1 && bp_exc==0 && immediate==0 && scaled==0)
        {
            if(source_sp!=1)
            {
                if(desti_sp!=1)
                {
                    for (Iterator it = Register8.keySet().iterator(); it.hasNext() && source_found==0;) 
                    {
                        String key = (String) it.next();
                        if(source.compareTo(key)==0)
                        {
                            RM=(String)Register8.get(key);
                            source_found=1;
                        }
                    }
                    for (Iterator it = Register16.keySet().iterator(); it.hasNext() && source_found==0;) 
                    {
                        String key = (String) it.next();
                        if(source.compareTo(key)==0)
                        {
                            RM=(String)Register16.get(key);
                            source_found=1;
                            if(mode_32==1)
                            {
                                prefix_reg="66";
                            }
                        }
                    }
                    for (Iterator it = Register32.keySet().iterator(); it.hasNext() && source_found==0;) 
                    {
                        String key = (String) it.next();
                        if(source.compareTo(key)==0)
                        {
                            RM=(String)Register32.get(key);
                            source_found=1;
                            if(mode_16==1)
                            {
                                prefix_reg="66";
                            }
                        }
                    }
                    for (Iterator it = SegmentReg.keySet().iterator(); it.hasNext() && source_found==0;) 
                    {
                        String key = (String) it.next();
                        if(source.compareTo(key)==0)
                        {
                            RM=(String)SegmentReg.get(key);
                            source_found=1;
                        }
                    }
                }
                if(desti_sp==1)
                {
                    //source_sp!=1
                    for (Iterator it = Register8.keySet().iterator(); it.hasNext() && source_found==0;) 
                    {
                        String key = (String) it.next();
                        if(source.compareTo(key)==0)
                        {
                            REG=(String)Register8.get(key);
                            source_found=1;
                        }
                    }
                    for (Iterator it = Register16.keySet().iterator(); it.hasNext() && source_found==0;) 
                    {
                        String key = (String) it.next();
                        if(source.compareTo(key)==0)
                        {
                            REG=(String)Register16.get(key);
                            source_found=1;
                            if(mode_32==1)
                            {
                                prefix_reg="66";
                            }
                        }
                    }
                    for (Iterator it = Register32.keySet().iterator(); it.hasNext() && source_found==0;) 
                    {
                        String key = (String) it.next();
                        if(source.compareTo(key)==0)
                        {
                            REG=(String)Register32.get(key);
                            source_found=1;
                            if(mode_16==1)
                            {
                                prefix_reg="66";
                            }
                        }
                    }
                    for (Iterator it = SegmentReg.keySet().iterator(); it.hasNext() && source_found==0;) 
                    {
                        String key = (String) it.next();
                        if(source.compareTo(key)==0)
                        {
                            REG=(String)SegmentReg.get(key);
                            source_found=1;
                        }
                    }
                   //search in 16 bit addr mode table
                    for (Iterator it = SpecialMode1.keySet().iterator(); it.hasNext() && desti_found==0;) 
                    {
                        String key = (String) it.next();
                        if(desti.compareTo(key)==0)
                        {
                            RM=(String)SpecialMode1.get(key);
                            desti_found=1;
                            if(mode_32==1)
                            {
                                prefix_addr="67";
                            }
                        }
                    }

                    //search in 32-bit addr mode table
                    for (Iterator it = SpecialMode2.keySet().iterator(); it.hasNext()&& desti_found==0;) 
                    {
                        String key = (String) it.next();
                        if(desti.compareTo(key)==0)
                        {
                            RM=(String)SpecialMode2.get(key);
                            desti_found=1;
                             if(mode_16==1)
                            {
                                prefix_addr="67";
                            }
                        }
                    }
                }

            }
            if(desti_sp!=1)
            {
                //search in normal register table
                for (Iterator it = Register8.keySet().iterator(); it.hasNext() && desti_found==0;) 
                {
                    String key = (String) it.next();
                    if(desti.compareTo(key)==0)
                    {
                        REG=(String)Register8.get(key);
                        desti_found=1;
                        W=0;
                        
                    }
                  
                }
                 for (Iterator it = Register16.keySet().iterator(); it.hasNext() && desti_found==0;) 
                {
                    String key = (String) it.next();
                    if(desti.compareTo(key)==0)
                    {
                        REG=(String)Register16.get(key);
                        desti_found=1;
                        W=1;
                        if(mode_32==1)
                        {
                            prefix_reg="66";
                        }
                    }
                  
                }
                for (Iterator it = Register32.keySet().iterator(); it.hasNext() && desti_found==0;) 
                {
                    String key = (String) it.next();
                    if(desti.compareTo(key)==0)
                    {
                        REG=(String)Register32.get(key);
                        desti_found=1;
                        W=1;
                        if(mode_16==1)
                        {
                            prefix_reg="66";
                        }
                    }
                  
                }

                //search in segment register table
                for (Iterator it = SegmentReg.keySet().iterator(); it.hasNext() && desti_found==0;) 
                {
                    String key = (String) it.next();
                    if(desti.compareTo(key)==0)
                    {
                        REG=(String)SegmentReg.get(key);
                        desti_found=1;
                        W=1;
                        
                    }

                }
                if(source_sp==1)//handling special tables search
                {
                    //search in 16 bit addr mode table
                    for (Iterator it = SpecialMode1.keySet().iterator(); it.hasNext() && source_found==0;) 
                    {
                        String key = (String) it.next();
                        if(source.compareTo(key)==0)
                        {
                            RM=(String)SpecialMode1.get(key);
                            source_found=1;
                            if(mode_32==1)
                            {
                                prefix_addr="67";
                            }
                        }
                    }
                    //search in 32-bit addr mode table
                    for (Iterator it = SpecialMode2.keySet().iterator(); it.hasNext() && source_found==0;) 
                    {
                        String key = (String) it.next();
                        if(source.compareTo(key)==0)
                        {
                            RM=(String)SpecialMode2.get(key);
                            source_found=1;
                            if(mode_16==1)
                            {
                                prefix_addr="67";
                            }
                        }
                    }
                }
            }
            bin=mov_opcode + D + W + MOD + REG + RM;
            System.out.println("\nbin:"+bin);
           if(MOD.compareTo("10")==0 && MOD.compareTo("01")==0)
           {
                addr_h=instr.substring(check_plus1+1, check_plus1+3);
                addr_l=instr.substring(check_plus1+3, check_plus1+5);
           }
        }
        //16-bit R/M memory-addressing mode(special addressing mode)
        System.out.println();
        System.out.println("\nW:"+W);
        System.out.println("\nMOD:"+MOD);
        System.out.println("\nMov opcode:"+ mov_opcode);
        System.out.println("\ndirection:"+D);
        System.out.println("\nreg:"+REG);
        System.out.println("\nrm:"+RM);
        System.out.println("\nBIN:"+bin);
        
        
        //total converted code
        if(prefix_reg!=null)
        {
            System.out.print(prefix_reg);
        }
        if(prefix_addr!=null)
        {
            System.out.print(prefix_addr);
        }
        int i=0;
        int len=0;
        len=bin.length();
        String Hexa_code=null;
        String temp=null, temp1=null;
        for(int c=1; c<=(len/4); c++)
        {
            temp=bin.substring(i, i+4);
            for (Iterator it = Hex.keySet().iterator(); it.hasNext() ;)
            {
                String key = (String) it.next();
                if(temp.compareTo(key)==0)
                {
                    temp1=(String)Hex.get(key);
                }
            }
            if(Hexa_code==null)
            {
                Hexa_code=temp1;
            }
            else
            {
                 Hexa_code=Hexa_code.concat(temp1);
            }
            i=i+4;
        }
            System.out.print(Hexa_code);
             if(data_l!=null)
            {
                System.out.print(data_l);
            }
            if(data_h!=null)
            {
                System.out.print(data_h);
            }
            if(addr_l!=null)
            {
                System.out.print(addr_l);
            }
           if(addr_h!=null)
            {
                System.out.print(addr_h);
            }
    }
}

