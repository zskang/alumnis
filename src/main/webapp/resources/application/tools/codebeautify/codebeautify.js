(function(){function a(d,c){var e=new b(d,c);return e.beautify()}function b(g,F){var e,J,t,ac,Z,C,an;var v,G,h;var O,af,j,r,A,am;var W;var aa,n,k,ao,z;var B;var d,L,w;var aq="";O="\n\r\t ".split("");af="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_$".split("");am="0123456789".split("");j="+ - * / % & ++ -- = += -= *= /= %= == === != !== > < >= <= >> << >>> >>>= >>= <<= && &= | || ! !! , : ? ^ ^= |= ::";j+=" <%= <% %> <?= <? ?>";j=j.split(" ");A="continue,try,throw,return,var,if,switch,case,default,for,while,break,function".split(",");L={BlockStatement:"BlockStatement",Statement:"Statement",ObjectLiteral:"ObjectLiteral",ArrayLiteral:"ArrayLiteral",ForInitializer:"ForInitializer",Conditional:"Conditional",Expression:"Expression"};d={TK_START_EXPR:aj,TK_END_EXPR:N,TK_START_BLOCK:X,TK_END_BLOCK:Y,TK_WORD:y,TK_SEMICOLON:M,TK_STRING:x,TK_EQUALS:R,TK_OPERATOR:ad,TK_COMMA:ah,TK_BLOCK_COMMENT:ag,TK_INLINE_COMMENT:V,TK_COMMENT:o,TK_DOT:ai,TK_UNKNOWN:ab};function l(at,au){return{mode:au,last_text:at?at.last_text:"",last_word:at?at.last_word:"",var_line:false,var_line_tainted:false,var_line_reindented:false,in_html_comment:false,multiline_array:false,if_block:false,do_block:false,do_while:false,in_case_statement:false,in_case:false,case_body:false,indentation_level:(at?at.indentation_level+((at.var_line&&at.var_line_reindented)?1:0):0),ternary_depth:0}}F=F?F:{};w={};if(F.space_after_anon_function!==undefined&&F.jslint_happy===undefined){F.jslint_happy=F.space_after_anon_function}if(F.braces_on_own_line!==undefined){w.brace_style=F.braces_on_own_line?"expand":"collapse"}w.brace_style=F.brace_style?F.brace_style:(w.brace_style?w.brace_style:"collapse");w.indent_size=F.indent_size?parseInt(F.indent_size,10):4;w.indent_char=F.indent_char?F.indent_char:" ";w.preserve_newlines=(F.preserve_newlines===undefined)?true:F.preserve_newlines;w.break_chained_methods=(F.break_chained_methods===undefined)?false:F.break_chained_methods;w.max_preserve_newlines=(F.max_preserve_newlines===undefined)?0:parseInt(F.max_preserve_newlines,10);w.jslint_happy=(F.jslint_happy===undefined)?false:F.jslint_happy;w.keep_array_indentation=(F.keep_array_indentation===undefined)?false:F.keep_array_indentation;w.space_before_conditional=(F.space_before_conditional===undefined)?true:F.space_before_conditional;w.unescape_strings=(F.unescape_strings===undefined)?false:F.unescape_strings;w.wrap_line_length=(F.wrap_line_length===undefined)?0:parseInt(F.wrap_line_length,10);an="";while(w.indent_size>0){an+=w.indent_char;w.indent_size-=1}while(g&&(g.charAt(0)===" "||g.charAt(0)==="\t")){aq+=g.charAt(0);g=g.substring(1)}e=g;B=g.length;Z="TK_START_BLOCK";C="";J=[];k=false;ao=false;z=[];h=[];q(L.BlockStatement);r=0;this.beautify=function(){var av,au,at,aw;while(true){av=ae();t=av[0];ac=av[1];if(ac==="TK_EOF"){break}at=w.keep_array_indentation&&T(v.mode);if(at){for(au=0;au<n;au+=1){I(true)}}else{aa=n>0;if(w.max_preserve_newlines&&n>w.max_preserve_newlines){n=w.max_preserve_newlines}if(w.preserve_newlines){if(n>1){I();for(au=1;au<n;au+=1){I(true)}}}}d[ac]();if(ac!=="TK_INLINE_COMMENT"&&ac!=="TK_COMMENT"&&ac!=="TK_UNKNOWN"){C=v.last_text;Z=ac;v.last_text=t}}aw=aq+J.join("").replace(/[\r\n ]+$/,"");return aw};function u(at){at=(at===undefined)?false:at;while(J.length&&(J[J.length-1]===" "||J[J.length-1]===an||J[J.length-1]===aq||(at&&(J[J.length-1]==="\n"||J[J.length-1]==="\r")))){J.pop()}}function c(at){return at.replace(/^\s\s*|\s\s*$/,"")}function al(av){av=av.replace(/\x0d/g,"");var au=[],at=av.indexOf("\n");while(at!==-1){au.push(av.substring(0,at));av=av.substring(at+1);at=av.indexOf("\n")}if(av.length){au.push(av)}return au}function ar(){return J.length&&J[J.length-1]==="\n"}function E(at,av){var au=at.length-1;if(au<0){au+=at.length}if(au>at.length-1){au=at.length-1}for(au++;au-->0;){if(au in at&&at[au]===av){return au}}return -1}function p(av){av=(av===undefined)?false:av;if(w.wrap_line_length&&!av){var at="";var au=0;var aw=E(J,"\n")+1;if(aw<J.length){at=J.slice(aw).join("");au=at.length+t.length+(ao?1:0);if(au>=w.wrap_line_length){av=true}}}if(((w.preserve_newlines&&aa)||av)&&!ar()){I(false,true);k=true;aa=false}}function I(at,au){k=false;ao=false;if(!au){if(v.last_text!==";"){while(v.mode===L.Statement&&!v.if_block){i()}}}if(v.mode===L.ArrayLiteral){v.multiline_array=true}if(!J.length){return}if(at||!ar()){J.push("\n")}}function D(){if(ar()){if(w.keep_array_indentation&&T(v.mode)&&z.length){J.push(z.join("")+"")}else{if(aq){J.push(aq)}m(v.indentation_level);m(v.var_line&&v.var_line_reindented);m(k)}}}function m(au){if(au===undefined){au=1}else{if(typeof au!=="number"){au=au?1:0}}if(v.last_text!==""){for(var at=0;at<au;at+=1){J.push(an)}}}function ak(){if(ao&&J.length){var at=J[J.length-1];if(!ar()&&at!==" "&&at!==an){J.push(" ")}}}function P(at){at=at||t;D();k=false;ak();ao=false;J.push(at)}function s(){v.indentation_level+=1}function q(at){if(v){h.push(v);G=v}else{G=l(null,at)}v=l(G,at)}function T(at){return at===L.ArrayLiteral}function K(at){return Q(at,[L.ArrayLiteral,L.Expression,L.ForInitializer,L.Conditional])}function i(){if(h.length>0){G=v;v=h.pop()}}function H(){if((v.last_text==="do"||(v.last_text==="else"&&t!=="if")||(Z==="TK_END_EXPR"&&(G.mode===L.ForInitializer||G.mode===L.Conditional)))){p();q(L.Statement);s();k=false;return true}return false}function ap(au,aw){for(var av=0;av<au.length;av++){var at=c(au[av]);if(at.charAt(0)!==aw){return false}}return true}function S(at){return Q(at,["case","return","do","if","throw","else"])}function Q(av,at){for(var au=0;au<at.length;au+=1){if(at[au]===av){return true}}return false}function f(aw){var at=false,au="",az=0,av="",ax=0,ay;while(at||az<aw.length){ay=aw.charAt(az);az++;if(at){at=false;if(ay==="x"){av=aw.substr(az,2);az+=2}else{if(ay==="u"){av=aw.substr(az,4);az+=4}else{au+="\\"+ay;continue}}if(!av.match(/^[0123456789abcdefABCDEF]+$/)){return aw}ax=parseInt(av,16);if(ax>=0&&ax<32){if(ay==="x"){au+="\\x"+av}else{au+="\\u"+av}continue}else{if(ax===34||ax===39||ax===92){au+="\\"+String.fromCharCode(ax)}else{if(ay==="x"&&ax>126&&ax<=255){return aw}else{au+=String.fromCharCode(ax)}}}}else{if(ay==="\\"){at=true}else{au+=ay}}}return au}function U(au){var at=r;var av=e.charAt(at);while(Q(av,O)&&av!==au){at++;if(at>=B){return false}av=e.charAt(at)}return av===au}function ae(){var aw,at;n=0;if(r>=B){return["","TK_EOF"]}aa=false;z=[];var aB=e.charAt(r);r+=1;while(Q(aB,O)){if(aB==="\n"){n+=1;z=[]}else{if(n){if(aB===an){z.push(an)}else{if(aB!=="\r"){z.push(" ")}}}}if(r>=B){return["","TK_EOF"]}aB=e.charAt(r);r+=1}if(Q(aB,af)){if(r<B){while(Q(e.charAt(r),af)){aB+=e.charAt(r);r+=1;if(r===B){break}}}if(r!==B&&aB.match(/^[0-9]+[Ee]$/)&&(e.charAt(r)==="-"||e.charAt(r)==="+")){var au=e.charAt(r);r+=1;var aD=ae();aB+=au+aD[0];return[aB,"TK_WORD"]}if(aB==="in"){return[aB,"TK_OPERATOR"]}return[aB,"TK_WORD"]}if(aB==="("||aB==="["){return[aB,"TK_START_EXPR"]}if(aB===")"||aB==="]"){return[aB,"TK_END_EXPR"]}if(aB==="{"){return[aB,"TK_START_BLOCK"]}if(aB==="}"){return[aB,"TK_END_BLOCK"]}if(aB===";"){return[aB,"TK_SEMICOLON"]}if(aB==="/"){var ax="";var aC=true;if(e.charAt(r)==="*"){r+=1;if(r<B){while(r<B&&!(e.charAt(r)==="*"&&e.charAt(r+1)&&e.charAt(r+1)==="/")){aB=e.charAt(r);ax+=aB;if(aB==="\n"||aB==="\r"){aC=false}r+=1;if(r>=B){break}}}r+=2;if(aC&&n===0){return["/*"+ax+"*/","TK_INLINE_COMMENT"]}else{return["/*"+ax+"*/","TK_BLOCK_COMMENT"]}}if(e.charAt(r)==="/"){ax=aB;while(e.charAt(r)!=="\r"&&e.charAt(r)!=="\n"){ax+=e.charAt(r);r+=1;if(r>=B){break}}return[ax,"TK_COMMENT"]}}if(aB==="'"||aB==='"'||(aB==="/"&&((Z==="TK_WORD"&&S(v.last_text))||(Z==="TK_END_EXPR"&&Q(G.mode,[L.Conditional,L.ForInitializer]))||(Q(Z,["TK_COMMENT","TK_START_EXPR","TK_START_BLOCK","TK_END_BLOCK","TK_OPERATOR","TK_EQUALS","TK_EOF","TK_SEMICOLON","TK_COMMA"]))))){var aE=aB,aA=false,az=false;at=aB;if(r<B){if(aE==="/"){var ay=false;while(aA||ay||e.charAt(r)!==aE){at+=e.charAt(r);if(!aA){aA=e.charAt(r)==="\\";if(e.charAt(r)==="["){ay=true}else{if(e.charAt(r)==="]"){ay=false}}}else{aA=false}r+=1;if(r>=B){return[at,"TK_STRING"]}}}else{while(aA||e.charAt(r)!==aE){at+=e.charAt(r);if(aA){if(e.charAt(r)==="x"||e.charAt(r)==="u"){az=true}aA=false}else{aA=e.charAt(r)==="\\"}r+=1;if(r>=B){return[at,"TK_STRING"]}}}}r+=1;at+=aE;if(az&&w.unescape_strings){at=f(at)}if(aE==="/"){while(r<B&&Q(e.charAt(r),af)){at+=e.charAt(r);r+=1}}return[at,"TK_STRING"]}if(aB==="#"){if(J.length===0&&e.charAt(r)==="!"){at=aB;while(r<B&&aB!=="\n"){aB=e.charAt(r);at+=aB;r+=1}return[c(at)+"\n","TK_UNKNOWN"]}var av="#";if(r<B&&Q(e.charAt(r),am)){do{aB=e.charAt(r);av+=aB;r+=1}while(r<B&&aB!=="#"&&aB!=="=");if(aB==="#"){}else{if(e.charAt(r)==="["&&e.charAt(r+1)==="]"){av+="[]";r+=2}else{if(e.charAt(r)==="{"&&e.charAt(r+1)==="}"){av+="{}";r+=2}}}return[av,"TK_WORD"]}}if(aB==="<"&&e.substring(r-1,r+3)==="<!--"){r+=3;aB="<!--";while(e.charAt(r)!=="\n"&&r<B){aB+=e.charAt(r);r++}v.in_html_comment=true;return[aB,"TK_COMMENT"]}if(aB==="-"&&v.in_html_comment&&e.substring(r-1,r+2)==="-->"){v.in_html_comment=false;r+=2;return["-->","TK_COMMENT"]}if(aB==="."){return[aB,"TK_DOT"]}if(Q(aB,j)){while(r<B&&Q(aB+e.charAt(r),j)){aB+=e.charAt(r);r+=1;if(r>=B){break}}if(aB===","){return[aB,"TK_COMMA"]}else{if(aB==="="){return[aB,"TK_EQUALS"]}else{return[aB,"TK_OPERATOR"]}}}return[aB,"TK_UNKNOWN"]}function aj(){if(H()){}if(t==="["){if(Z==="TK_WORD"||v.last_text===")"){if(Q(v.last_text,A)){ao=true}q(L.Expression);P();return}if(T(v.mode)){if((v.last_text==="[")||(C==="]"&&v.last_text===",")){if(!w.keep_array_indentation){I()}}}}else{if(v.last_text==="for"){q(L.ForInitializer)}else{if(Q(v.last_text,["if","while"])){q(L.Conditional)}else{q(L.Expression)}}}if(v.last_text===";"||Z==="TK_START_BLOCK"){I()}else{if(Z==="TK_END_EXPR"||Z==="TK_START_EXPR"||Z==="TK_END_BLOCK"||v.last_text==="."){if(aa){I()}}else{if(Z!=="TK_WORD"&&Z!=="TK_OPERATOR"){ao=true}else{if(v.last_word==="function"||v.last_word==="typeof"){if(w.jslint_happy){ao=true}}else{if(Q(v.last_text,A)||v.last_text==="catch"){if(w.space_before_conditional){ao=true}}}}}}if(t==="("){if(Z==="TK_EQUALS"||Z==="TK_OPERATOR"){if(v.mode!==L.ObjectLiteral){p()}}}P();if(t==="["){q(L.ArrayLiteral);s()}}function N(){while(v.mode===L.Statement){i()}if(t==="]"&&T(v.mode)&&v.multiline_array&&!w.keep_array_indentation){I()}i();P();if(v.do_while&&G.mode===L.Conditional){G.mode=L.Expression;v.do_block=false;v.do_while=false}}function X(){q(L.BlockStatement);var at=U("}");if(w.brace_style==="expand-strict"){if(!at){I()}}else{if(w.brace_style==="expand"){if(Z!=="TK_OPERATOR"){if(Z==="TK_EQUALS"||(S(v.last_text)&&v.last_text!=="else")){ao=true}else{I()}}}else{if(Z!=="TK_OPERATOR"&&Z!=="TK_START_EXPR"){if(Z==="TK_START_BLOCK"){I()}else{ao=true}}else{if(T(G.mode)&&v.last_text===","){if(C==="}"){ao=true}else{I()}}}}}P();s()}function Y(){while(v.mode===L.Statement){i()}i();if(w.brace_style==="expand"||w.brace_style==="expand-strict"){if(Z!=="TK_START_BLOCK"){I()}}else{if(Z!=="TK_START_BLOCK"){if(T(v.mode)&&w.keep_array_indentation){w.keep_array_indentation=false;I();w.keep_array_indentation=true}else{I()}}}P()}function y(){if(H()){}else{if(aa&&!K(v.mode)&&(Z!=="TK_OPERATOR"||(v.last_text==="--"||v.last_text==="++"))&&Z!=="TK_EQUALS"&&(w.preserve_newlines||v.last_text!=="var")){I()}}if(v.do_block&&!v.do_while){if(t==="while"){ao=true;P();ao=true;v.do_while=true;return}else{I();v.do_block=false}}if(v.if_block){if(t!=="else"){while(v.mode===L.Statement){i()}v.if_block=false}}if(t==="function"){if(v.var_line&&Z!=="TK_EQUALS"){v.var_line_reindented=true}if((ar()||v.last_text===";")&&v.last_text!=="{"&&Z!=="TK_BLOCK_COMMENT"&&Z!=="TK_COMMENT"){n=ar()?n:0;if(!w.preserve_newlines){n=1}for(var at=0;at<2-n;at++){I(true)}}if(Z==="TK_WORD"){if(v.last_text==="get"||v.last_text==="set"||v.last_text==="new"||v.last_text==="return"){ao=true}else{I()}}else{if(Z==="TK_OPERATOR"||v.last_text==="="){ao=true}else{if(K(v.mode)){}else{I()}}}P();v.last_word=t;return}if(t==="case"||(t==="default"&&v.in_case_statement)){I();if(v.case_body||w.jslint_happy){v.indentation_level--;v.case_body=false}P();v.in_case=true;v.in_case_statement=true;return}W="NONE";if(Z==="TK_END_BLOCK"){if(!Q(t,["else","catch","finally"])){W="NEWLINE"}else{if(w.brace_style==="expand"||w.brace_style==="end-expand"||w.brace_style==="expand-strict"){W="NEWLINE"}else{W="SPACE";ao=true}}}else{if(Z==="TK_SEMICOLON"&&v.mode===L.BlockStatement){W="NEWLINE"}else{if(Z==="TK_SEMICOLON"&&K(v.mode)){W="SPACE"}else{if(Z==="TK_STRING"){W="NEWLINE"}else{if(Z==="TK_WORD"){W="SPACE"}else{if(Z==="TK_START_BLOCK"){W="NEWLINE"}else{if(Z==="TK_END_EXPR"){ao=true;W="NEWLINE"}}}}}}}if(Q(t,A)&&v.last_text!==")"){if(v.last_text==="else"){W="SPACE"}else{W="NEWLINE"}}if(Z==="TK_COMMA"||Z==="TK_START_EXPR"||Z==="TK_EQUALS"||Z==="TK_OPERATOR"){if(v.mode!==L.ObjectLiteral){p()}}if(Q(t,["else","catch","finally"])){if(Z!=="TK_END_BLOCK"||w.brace_style==="expand"||w.brace_style==="end-expand"||w.brace_style==="expand-strict"){I()}else{u(true);if(J[J.length-1]!=="}"){I()}ao=true}}else{if(W==="NEWLINE"){if(S(v.last_text)){ao=true}else{if(Z!=="TK_END_EXPR"){if((Z!=="TK_START_EXPR"||t!=="var")&&v.last_text!==":"){if(t==="if"&&v.last_word==="else"&&v.last_text!=="{"){ao=true}else{v.var_line=false;v.var_line_reindented=false;I()}}}else{if(Q(t,A)&&v.last_text!==")"){v.var_line=false;v.var_line_reindented=false;I()}}}}else{if(T(v.mode)&&v.last_text===","&&C==="}"){I()}else{if(W==="SPACE"){ao=true}}}}P();v.last_word=t;if(t==="var"){v.var_line=true;v.var_line_reindented=false;v.var_line_tainted=false}if(t==="do"){v.do_block=true}if(t==="if"){v.if_block=true}}function M(){while(v.mode===L.Statement&&!v.if_block){i()}P();v.var_line=false;v.var_line_reindented=false;if(v.mode===L.ObjectLiteral){v.mode=L.BlockStatement}}function x(){if(H()){ao=true}else{if(Z==="TK_WORD"){ao=true}else{if(Z==="TK_COMMA"||Z==="TK_START_EXPR"||Z==="TK_EQUALS"||Z==="TK_OPERATOR"){if(v.mode!==L.ObjectLiteral){p()}}else{I()}}}P()}function R(){if(v.var_line){v.var_line_tainted=true}ao=true;P();ao=true}function ah(){if(v.var_line){if(K(v.mode)||Z==="TK_END_BLOCK"){v.var_line_tainted=false}if(v.var_line){v.var_line_reindented=true}P();if(v.var_line_tainted){v.var_line_tainted=false;I()}else{ao=true}return}if(Z==="TK_END_BLOCK"&&v.mode!==L.Expression){P();if(v.mode===L.ObjectLiteral&&v.last_text==="}"){I()}else{ao=true}}else{if(v.mode===L.ObjectLiteral){P();I()}else{P();ao=true}}}function ad(){var au=true;var at=true;if(S(v.last_text)){ao=true;P();return}if(t==="*"&&Z==="TK_DOT"&&!C.match(/^\d+$/)){P();return}if(t===":"&&v.in_case){v.case_body=true;s();P();I();v.in_case=false;return}if(t==="::"){P();return}if(aa&&(t==="--"||t==="++")){I()}if(Q(t,["--","++","!"])||(Q(t,["-","+"])&&(Q(Z,["TK_START_BLOCK","TK_START_EXPR","TK_EQUALS","TK_OPERATOR"])||Q(v.last_text,A)||v.last_text===","))){au=false;at=false;if(v.last_text===";"&&K(v.mode)){au=true}if(Z==="TK_WORD"&&Q(v.last_text,A)){au=true}if((v.mode===L.BlockStatement||v.mode===L.Statement)&&(v.last_text==="{"||v.last_text===";")){I()}}else{if(t===":"){if(v.ternary_depth===0){if(v.mode===L.BlockStatement){v.mode=L.ObjectLiteral}au=false}else{v.ternary_depth-=1}}else{if(t==="?"){v.ternary_depth+=1}}}ao=ao||au;P();ao=at}function ag(){var at=al(t);var au;if(ap(at.slice(1),"*")){I(false,true);P(at[0]);for(au=1;au<at.length;au++){I(false,true);P(" "+c(at[au]))}}else{if(at.length>1){I(false,true)}else{if(Z==="TK_END_BLOCK"){I(false,true)}else{ao=true}}P(at[0]);J.push("\n");for(au=1;au<at.length;au++){J.push(at[au]);J.push("\n")}}if(!U("\n")){I(false,true)}}function V(){ao=true;P();ao=true}function o(){if(aa){I(false,true)}if(v.last_text===","&&!aa){u(true)}ao=true;P();I(false,true)}function ai(){if(S(v.last_text)){ao=true}else{p(v.last_text===")"&&w.break_chained_methods)}P()}function ab(){P();if(t[t.length-1]==="\n"){I()}}}if(typeof define==="function"){define(function(d,c,e){c.js_beautify=a})}else{if(typeof exports!=="undefined"){exports.js_beautify=a}else{if(typeof window!=="undefined"){window.js_beautify=a}}}}());(function(){function a(v,e){e=e||{};var t=e.indent_size||4;var c=e.indent_char||" ";if(typeof t==="string"){t=parseInt(t,10)}var h=/^\s+$/;var f=/[\w$\-_]/;var j=-1,n;function r(){n=v.charAt(++j);return n}function s(){return v.charAt(j+1)}function x(z){var A=j;while(r()){if(n==="\\"){r();r()}else{if(n===z){break}else{if(n==="\n"){break}}}}return v.substring(A,j+1)}function b(){var z=j;while(h.test(s())){j++}return j!==z}function y(){var z=j;do{}while(h.test(r()));return j!==z+1}function q(){var z=j;r();while(r()){if(n==="*"&&s()==="/"){j++;break}}return v.substring(z,j+1)}function k(z){return v.substring(j-z.length,j).toLowerCase()===z}var p=v.match(/^[\r\n]*[\t ]*/)[0];var i=Array(t+1).join(c);var u=0;function m(){u++;p+=i}function o(){u--;p=p.slice(0,-t)}var d={};d["{"]=function(z){d.singleSpace();l.push(z);d.newLine()};d["}"]=function(z){d.newLine();l.push(z);d.newLine()};d.newLine=function(z){if(!z){while(h.test(l[l.length-1])){l.pop()}}if(l.length){l.push("\n")}if(p){l.push(p)}};d.singleSpace=function(){if(l.length&&!h.test(l[l.length-1])){l.push(" ")}};var l=[];if(p){l.push(p)}while(true){var g=y();if(!n){break}if(n==="{"){m();d["{"](n)}else{if(n==="}"){o();d["}"](n)}else{if(n==='"'||n==="'"){l.push(x(n))}else{if(n===";"){l.push(n,"\n",p)}else{if(n==="/"&&s()==="*"){d.newLine();l.push(q(),"\n",p)}else{if(n==="("){if(k("url")){l.push(n);b();if(r()){if(n!==")"&&n!=='"'&&n!=="'"){l.push(x(")"))}else{j--}}}else{if(g){d.singleSpace()}l.push(n);b()}}else{if(n===")"){l.push(n)}else{if(n===","){b();l.push(n);d.singleSpace()}else{if(n==="]"){l.push(n)}else{if(n==="["||n==="="){b();l.push(n)}else{if(g){d.singleSpace()}l.push(n)}}}}}}}}}}}var w=l.join("").replace(/[\n ]+$/,"");return w}if(typeof define==="function"){define(function(c,b,d){b.css_beautify=a})}else{if(typeof exports!=="undefined"){exports.css_beautify=a}else{if(typeof window!=="undefined"){window.css_beautify=a}}}}());(function(){function b(v,g,p,h){var z,s,q,j,k,y;g=g||{};s=g.indent_size||4;q=g.indent_char||" ";k=g.brace_style||"collapse";j=g.max_char===0?Infinity:g.max_char||250;y=g.unformatted||["a","span","bdo","em","strong","dfn","code","samp","kbd","var","cite","abbr","acronym","q","sub","sup","tt","i","b","big","small","u","s","strike","font","ins","del","pre","address","dt","h1","h2","h3","h4","h5","h6"];function f(){this.pos=0;this.token="";this.current_mode="CONTENT";this.tags={parent:"parent1",parentcount:1,parent1:""};this.tag_type="";this.token_text=this.last_token=this.last_text=this.token_type="";this.Utils={whitespace:"\n\r\t ".split(""),single_token:"br,input,link,meta,!doctype,basefont,base,area,hr,wbr,param,img,isindex,?xml,embed,?php,?,?=".split(","),extra_liners:"head,body,/html".split(","),in_array:function(B,t){for(var A=0;A<t.length;A++){if(B===t[A]){return true}}return false}};this.get_content=function(){var t="",B=[],C=false;while(this.input.charAt(this.pos)!=="<"){if(this.pos>=this.input.length){return B.length?B.join(""):["","TK_EOF"]}t=this.input.charAt(this.pos);this.pos++;this.line_char_count++;if(this.Utils.in_array(t,this.Utils.whitespace)){if(B.length){C=true}this.line_char_count--;continue}else{if(C){if(this.line_char_count>=this.max_char){B.push("\n");for(var A=0;A<this.indent_level;A++){B.push(this.indent_string)}this.line_char_count=0}else{B.push(" ");this.line_char_count++}C=false}}B.push(t)}return B.length?B.join(""):""};this.get_contents_to=function(B){if(this.pos===this.input.length){return["","TK_EOF"]}var t="";var C="";var D=new RegExp("</"+B+"\\s*>","igm");D.lastIndex=this.pos;var A=D.exec(this.input);var E=A?A.index:this.input.length;if(this.pos<E){C=this.input.substring(this.pos,E);this.pos=E}return C};this.record_tag=function(t){if(this.tags[t+"count"]){this.tags[t+"count"]++;this.tags[t+this.tags[t+"count"]]=this.indent_level}else{this.tags[t+"count"]=1;this.tags[t+this.tags[t+"count"]]=this.indent_level}this.tags[t+this.tags[t+"count"]+"parent"]=this.tags.parent;this.tags.parent=t+this.tags[t+"count"]};this.retrieve_tag=function(t){if(this.tags[t+"count"]){var A=this.tags.parent;while(A){if(t+this.tags[t+"count"]===A){break}A=this.tags[A+"parent"]}if(A){this.indent_level=this.tags[t+this.tags[t+"count"]];this.tags.parent=this.tags[A+"parent"]}delete this.tags[t+this.tags[t+"count"]+"parent"];delete this.tags[t+this.tags[t+"count"]];if(this.tags[t+"count"]===1){delete this.tags[t+"count"]}else{this.tags[t+"count"]--}}};this.get_tag=function(J){var E="",G=[],F="",A=false,I,D,B=this.pos,t=this.line_char_count;J=J!==undefined?J:false;do{if(this.pos>=this.input.length){if(J){this.pos=B;this.line_char_count=t}return G.length?G.join(""):["","TK_EOF"]}E=this.input.charAt(this.pos);this.pos++;this.line_char_count++;if(this.Utils.in_array(E,this.Utils.whitespace)){A=true;this.line_char_count--;continue}if(E==="'"||E==='"'){if(!G[1]||G[1]!=="!"){E+=this.get_unformatted(E);A=true}}if(E==="="){A=false}if(G.length&&G[G.length-1]!=="="&&E!==">"&&A){if(this.line_char_count>=this.max_char){this.print_newline(false,G);this.line_char_count=0}else{G.push(" ");this.line_char_count++}A=false}if(E==="<"){I=this.pos-1}G.push(E)}while(E!==">");var K=G.join("");var C;if(K.indexOf(" ")!==-1){C=K.indexOf(" ")}else{C=K.indexOf(">")}var H=K.substring(1,C).toLowerCase();if(K.charAt(K.length-2)==="/"||this.Utils.in_array(H,this.Utils.single_token)){if(!J){this.tag_type="SINGLE"}}else{if(H==="script"){if(!J){this.record_tag(H);this.tag_type="SCRIPT"}}else{if(H==="style"){if(!J){this.record_tag(H);this.tag_type="STYLE"}}else{if(this.is_unformatted(H,y)){F=this.get_unformatted("</"+H+">",K);G.push(F);if(I>0&&this.Utils.in_array(this.input.charAt(I-1),this.Utils.whitespace)){G.splice(0,0,this.input.charAt(I-1))}D=this.pos-1;if(this.Utils.in_array(this.input.charAt(D+1),this.Utils.whitespace)){G.push(this.input.charAt(D+1))}this.tag_type="SINGLE"}else{if(H.charAt(0)==="!"){if(H.indexOf("[if")!==-1){if(K.indexOf("!IE")!==-1){F=this.get_unformatted("-->",K);G.push(F)}if(!J){this.tag_type="START"}}else{if(H.indexOf("[endif")!==-1){this.tag_type="END";this.unindent()}else{if(H.indexOf("[cdata[")!==-1){F=this.get_unformatted("]]>",K);G.push(F);if(!J){this.tag_type="SINGLE"}}else{F=this.get_unformatted("-->",K);G.push(F);this.tag_type="SINGLE"}}}}else{if(!J){if(H.charAt(0)==="/"){this.retrieve_tag(H.substring(1));this.tag_type="END"}else{this.record_tag(H);this.tag_type="START"}if(this.Utils.in_array(H,this.Utils.extra_liners)){this.print_newline(true,this.output)}}}}}}}if(J){this.pos=B;this.line_char_count=t}return G.join("")};this.get_unformatted=function(A,B){if(B&&B.toLowerCase().indexOf(A)!==-1){return""}var t="";var C="";var D=true;do{if(this.pos>=this.input.length){return C}t=this.input.charAt(this.pos);this.pos++;if(this.Utils.in_array(t,this.Utils.whitespace)){if(!D){this.line_char_count--;continue}if(t==="\n"||t==="\r"){C+="\n";this.line_char_count=0;continue}}C+=t;this.line_char_count++;D=true}while(C.toLowerCase().indexOf(A)===-1);return C};this.get_token=function(){var t;if(this.last_token==="TK_TAG_SCRIPT"||this.last_token==="TK_TAG_STYLE"){var A=this.last_token.substr(7);t=this.get_contents_to(A);if(typeof t!=="string"){return t}return[t,"TK_"+A]}if(this.current_mode==="CONTENT"){t=this.get_content();if(typeof t!=="string"){return t}else{return[t,"TK_CONTENT"]}}if(this.current_mode==="TAG"){t=this.get_tag();if(typeof t!=="string"){return t}else{var B="TK_TAG_"+this.tag_type;return[t,B]}}};this.get_full_indent=function(t){t=this.indent_level+t||0;if(t<1){return""}return Array(t+1).join(this.indent_string)};this.is_unformatted=function(B,A){if(!this.Utils.in_array(B,A)){return false}if(B.toLowerCase()!=="a"||!this.Utils.in_array("a",A)){return true}var C=this.get_tag(true);var t=(C||"").match(/^\s*<\s*\/?([a-z]*)\s*[^>]*>\s*$/);if(!t||this.Utils.in_array(t,A)){return true}else{return false}};this.printer=function(C,B,t,E,D){this.input=C||"";this.output=[];this.indent_character=B;this.indent_string="";this.indent_size=t;this.brace_style=D;this.indent_level=0;this.max_char=E;this.line_char_count=0;for(var A=0;A<this.indent_size;A++){this.indent_string+=this.indent_character}this.print_newline=function(H,F){this.line_char_count=0;if(!F||!F.length){return}if(!H){while(this.Utils.in_array(F[F.length-1],this.Utils.whitespace)){F.pop()}}F.push("\n");for(var G=0;G<this.indent_level;G++){F.push(this.indent_string)}};this.print_token=function(F){this.output.push(F)};this.indent=function(){this.indent_level++};this.unindent=function(){if(this.indent_level>0){this.indent_level--}}};return this}z=new f();z.printer(v,q,s,j,k);while(true){var m=z.get_token();z.token_text=m[0];z.token_type=m[1];if(z.token_type==="TK_EOF"){break}switch(z.token_type){case"TK_TAG_START":z.print_newline(false,z.output);z.print_token(z.token_text);z.indent();z.current_mode="CONTENT";break;case"TK_TAG_STYLE":case"TK_TAG_SCRIPT":z.print_newline(false,z.output);z.print_token(z.token_text);z.current_mode="CONTENT";break;case"TK_TAG_END":if(z.last_token==="TK_CONTENT"&&z.last_text===""){var x=z.token_text.match(/\w+/)[0];var o=z.output[z.output.length-1].match(/<\s*(\w+)/);if(o===null||o[1]!==x){z.print_newline(true,z.output)}}z.print_token(z.token_text);z.current_mode="CONTENT";break;case"TK_TAG_SINGLE":var e=z.token_text.match(/^\s*<([a-z]+)/i);if(!e||!z.Utils.in_array(e[1],y)){z.print_newline(false,z.output)}z.print_token(z.token_text);z.current_mode="CONTENT";break;case"TK_CONTENT":if(z.token_text!==""){z.print_token(z.token_text)}z.current_mode="TAG";break;case"TK_STYLE":case"TK_SCRIPT":if(z.token_text!==""){z.output.push("\n");var n=z.token_text,u,l=1;if(z.token_type==="TK_SCRIPT"){u=typeof p==="function"&&p}else{if(z.token_type==="TK_STYLE"){u=typeof h==="function"&&h}}if(g.indent_scripts==="keep"){l=0}else{if(g.indent_scripts==="separate"){l=-z.indent_level}}var d=z.get_full_indent(l);if(u){n=u(n.replace(/^\s*/,d),g)}else{var i=n.match(/^\s*/)[0];var r=i.match(/[^\n\r]*$/)[0].split(z.indent_string).length-1;var w=z.get_full_indent(l-r);n=n.replace(/^\s*/,d).replace(/\r\n|\r|\n/g,"\n"+w).replace(/\s*$/,"")}if(n){z.print_token(n);z.print_newline(true,z.output)}}z.current_mode="TAG";break}z.last_token=z.token_type;z.last_text=z.token_text}return z.output.join("")}if(typeof define==="function"){define(function(f,e,g){var d=f("./beautify.js").js_beautify;var h=f("./beautify-css.js").css_beautify;e.html_beautify=function(j,i){return b(j,i,d,h)}})}else{if(typeof exports!=="undefined"){var a=require("./beautify.js").js_beautify;var c=require("./beautify-css.js").css_beautify;exports.html_beautify=function(e,d){return b(e,d,a,c)}}else{if(typeof window!=="undefined"){window.html_beautify=function(e,d){return b(e,d,window.js_beautify,window.css_beautify)}}}}}());window.registNS("FeHelper");FeHelper.CodeBeautify=(function(){var b={brace_style:"collapse",break_chained_methods:false,indent_char:" ",indent_scripts:"keep",indent_size:"4",keep_array_indentation:true,preserve_newlines:true,space_after_anon_function:true,space_before_conditional:true,unescape_strings:false,wrap_line_length:"120"};var a="Javascript";var c=function(){$('input[name="codeType"]').click(function(f){a=this.value;$("#codeTitle").html(this.value)});$("#btnFormat").click(function(i){if(a=="Javascript"){var h=js_beautify($("#codeSource").val(),b);h=h.replace(/>/g,"&gt;").replace(/</g,"&lt;");h='<pre class="brush: js;toolbar:false;">'+h+"</pre>";$("#jfContent").html(h)}else{if(a=="CSS"){var g=css_beautify($("#codeSource").val());g='<pre class="brush: css;toolbar:false;">'+g+"</pre>";$("#jfContent").html(g)}else{if(a=="HTML"){var f=html_beautify($("#codeSource").val());f='<pre class="brush: html;toolbar:false;">'+f+"</pre>";$("#jfContent").html(f)}}}SyntaxHighlighter.highlight()})};var d=function(){$(function(){jQuery("#codeSource").focus();c()})};return{init:d}})();FeHelper.CodeBeautify.init();