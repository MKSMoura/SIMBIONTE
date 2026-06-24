package com.example.simbionte.core.engine

import com.example.simbionte.core.engine.FragmentRole.*

object FragmentCatalog {

    val aberturas: Map<String, List<Fragment>> = mapOf(
        "first_meeting" to listOf(
            Fragment("Que legal te conhecer", ABERTURA, "first_meeting"),
            Fragment("É um prazer", ABERTURA, "first_meeting"),
            Fragment("Fico feliz que você está aqui", ABERTURA, "first_meeting"),
            Fragment("Que bom te encontrar", ABERTURA, "first_meeting"),
            Fragment("Que surpresa boa", ABERTURA, "first_meeting"),
            Fragment("É uma honra", ABERTURA, "first_meeting"),
            Fragment("Que massa te conhecer", ABERTURA, "first_meeting"),
            Fragment("Olá", ABERTURA, "first_meeting")
        ),
        "greeting" to listOf(
            Fragment("Que bom ter você aqui", ABERTURA, "greeting"),
            Fragment("Estou aqui", ABERTURA, "greeting"),
            Fragment("Presença confirmada", ABERTURA, "greeting"),
            Fragment("Opa", ABERTURA, "greeting"),
            Fragment("Fala aí", ABERTURA, "greeting"),
            Fragment("E aí", ABERTURA, "greeting"),
            Fragment("Opa", ABERTURA, "greeting"),
            Fragment("Que bom te ver de novo", ABERTURA, "greeting")
        ),
        "greeting_warm" to listOf(
            Fragment("Que bom te ver", ABERTURA, "greeting_warm"),
            Fragment("Opa", ABERTURA, "greeting_warm"),
            Fragment("Olha quem voltou", ABERTURA, "greeting_warm"),
            Fragment("E aí", ABERTURA, "greeting_warm"),
            Fragment("Fala aí", ABERTURA, "greeting_warm")
        ),
        "short_question" to listOf(
            Fragment("Essa é uma boa pergunta", ABERTURA, "short_question"),
            Fragment("Interessante", ABERTURA, "short_question"),
            Fragment("Hmm", ABERTURA, "short_question"),
            Fragment("Pergunta boa", ABERTURA, "short_question"),
            Fragment("Que questão interessante", ABERTURA, "short_question"),
            Fragment("Isso me faz pensar", ABERTURA, "short_question"),
            Fragment("Hmm", ABERTURA, "short_question"),
            Fragment("Boa pergunta", ABERTURA, "short_question")
        ),
        "short_prompt" to listOf(
            Fragment("Estou aqui", ABERTURA, "short_prompt"),
            Fragment("Presente", ABERTURA, "short_prompt"),
            Fragment("Estou aqui, só escutando", ABERTURA, "short_prompt"),
            Fragment("Tô aqui", ABERTURA, "short_prompt"),
            Fragment("Disponível", ABERTURA, "short_prompt"),
            Fragment("Tô por aqui", ABERTURA, "short_prompt"),
            Fragment("Só te ouvindo", ABERTURA, "short_prompt"),
            Fragment("Presente", ABERTURA, "short_prompt")
        ),
        "drift_revisit" to listOf(
            Fragment("Isso me lembra \$tagNote", ABERTURA, "drift_revisit"),
            Fragment("Você tocou em \$tagNote de novo", ABERTURA, "drift_revisit"),
            Fragment("\$tagNote apareceu de novo", ABERTURA, "drift_revisit"),
            Fragment("\$tagNote de novo", ABERTURA, "drift_revisit"),
            Fragment("É a segunda vez que \$tagNote aparece", ABERTURA, "drift_revisit"),
            Fragment("Tem \$tagNote de novo no que você disse", ABERTURA, "drift_revisit")
        ),
        "drift" to listOf(
            Fragment("Estou acompanhando", ABERTURA, "drift"),
            Fragment("Percebo que o assunto tomou outro caminho", ABERTURA, "drift"),
            Fragment("Sinto que você está indo para outra direção agora", ABERTURA, "drift"),
            Fragment("Percebi que mudou de rota", ABERTURA, "drift"),
            Fragment("O rumo mudou", ABERTURA, "drift"),
            Fragment("Novo caminho", ABERTURA, "drift")
        ),
        "return_response" to listOf(
            Fragment("Isso me parece familiar", ABERTURA, "return_response"),
            Fragment("Você já tinha tocado nesse ponto antes", ABERTURA, "return_response"),
            Fragment("Isso me remete a algo que conversamos anteriormente", ABERTURA, "return_response"),
            Fragment("\$tagNote de novo", ABERTURA, "return_response"),
            Fragment("Você trouxe \$tagNote outra vez", ABERTURA, "return_response"),
            Fragment("Lembrei na hora de \$tagNote", ABERTURA, "return_response")
        ),
        "flow_high" to listOf(
            Fragment("Sim, entendi", ABERTURA, "flow_high"),
            Fragment("Perfeito", ABERTURA, "flow_high"),
            Fragment("Captei", ABERTURA, "flow_high"),
            Fragment("Tô ligado", ABERTURA, "flow_high"),
            Fragment("Entendi", ABERTURA, "flow_high"),
            Fragment("Fechou", ABERTURA, "flow_high")
        ),
        "flow_med" to listOf(
            Fragment("Entendi", ABERTURA, "flow_med"),
            Fragment("Sim, faz sentido", ABERTURA, "flow_med"),
            Fragment("Estou seguindo sua linha de raciocínio", ABERTURA, "flow_med"),
            Fragment("Tô contigo", ABERTURA, "flow_med"),
            Fragment("Faz sentido", ABERTURA, "flow_med"),
            Fragment("Sei exatamente o que você quer dizer", ABERTURA, "flow_med")
        ),
        "contradiction" to listOf(
            Fragment("Interessante", ABERTURA, "contradiction"),
            Fragment("Você tá ajustando sua posição sobre algo que disse antes", ABERTURA, "contradiction"),
            Fragment("Percebo que sua visão mudou desde a última vez", ABERTURA, "contradiction"),
            Fragment("Você já disse algo diferente antes sobre isso", ABERTURA, "contradiction"),
            Fragment("Isso não é exatamente o que você tinha dito da última vez", ABERTURA, "contradiction"),
            Fragment("Sinto que você tá revendo uma posição anterior", ABERTURA, "contradiction")
        ),
        "continuation" to listOf(
            Fragment("Sim, isso conecta com o que você disse antes", ABERTURA, "continuation"),
            Fragment("Isso se conecta com o que estávamos conversando", ABERTURA, "continuation"),
            Fragment("É uma continuação natural do seu pensamento anterior", ABERTURA, "continuation"),
            Fragment("Tô vendo o fio", ABERTURA, "continuation"),
            Fragment("Isso é um desdobramento direto do que você tava dizendo", ABERTURA, "continuation"),
            Fragment("Percebo que isso nasceu do que você falou antes", ABERTURA, "continuation")
        ),
        "side_thought" to listOf(
            Fragment("Isso me parece uma ramificação interessante", ABERTURA, "side_thought"),
            Fragment("Você está explorando um desdobramento do assunto anterior", ABERTURA, "side_thought"),
            Fragment("Essa ideia conecta com o que você disse antes, mas por um ângulo diferente", ABERTURA, "side_thought"),
            Fragment("Isso é uma tangente interessante do que você tava falando", ABERTURA, "side_thought"),
            Fragment("Você seguiu um desvio do assunto principal", ABERTURA, "side_thought"),
            Fragment("Isso veio como um desdobramento do que você trouxe antes", ABERTURA, "side_thought")
        ),
        "reflection" to listOf(
            Fragment("Você elaborou bem esse pensamento", ABERTURA, "reflection"),
            Fragment("Isso é denso", ABERTURA, "reflection"),
            Fragment("Gosto da profundidade disso", ABERTURA, "reflection"),
            Fragment("Isso que você trouxe agora é profundo", ABERTURA, "reflection"),
            Fragment("Você foi fundo nessa reflexão", ABERTURA, "reflection"),
            Fragment("Tem densidade no que você disse", ABERTURA, "reflection")
        ),
        "question" to listOf(
            Fragment("Ótima pergunta", ABERTURA, "question"),
            Fragment("É uma boa questão", ABERTURA, "question"),
            Fragment("Interessante", ABERTURA, "question"),
            Fragment("Pergunta boa", ABERTURA, "question"),
            Fragment("Hmm", ABERTURA, "question"),
            Fragment("Boa pergunta", ABERTURA, "question")
        ),
        "default" to listOf(
            Fragment("Entendido", ABERTURA, "default"),
            Fragment("Recebi", ABERTURA, "default"),
            Fragment("Anotado", ABERTURA, "default"),
            Fragment("Tudo bem", ABERTURA, "default"),
            Fragment("Beleza", ABERTURA, "default"),
            Fragment("Ciente", ABERTURA, "default"),
            Fragment("Ok", ABERTURA, "default"),
            Fragment("Fechou", ABERTURA, "default")
        ),
        "affirmation" to listOf(
            Fragment("Entendi", ABERTURA, "affirmation"),
            Fragment("Sim, faz sentido", ABERTURA, "affirmation"),
            Fragment("Parece que estamos na mesma página", ABERTURA, "affirmation"),
            Fragment("Beleza", ABERTURA, "affirmation"),
            Fragment("Fechou", ABERTURA, "affirmation"),
            Fragment("Tranquilo", ABERTURA, "affirmation")
        ),
        "denial" to listOf(
            Fragment("Entendo", ABERTURA, "denial"),
            Fragment("Tudo bem", ABERTURA, "denial"),
            Fragment("Entendo seu ponto", ABERTURA, "denial"),
            Fragment("Entendo", ABERTURA, "denial"),
            Fragment("Tudo bem", ABERTURA, "denial"),
            Fragment("Sem problema", ABERTURA, "denial")
        ),
        "denial_warm" to listOf(
            Fragment("Tudo bem, sério", ABERTURA, "denial_warm"),
            Fragment("Pode ficar tranquilo", ABERTURA, "denial_warm"),
            Fragment("Não se preocupa com isso", ABERTURA, "denial_warm"),
            Fragment("Relaxa, tá tudo bem", ABERTURA, "denial_warm")
        ),
        "low_sentiment" to listOf(
            Fragment("Sinto que isso pesa em você", ABERTURA, "low_sentiment"),
            Fragment("Isso parece difícil", ABERTURA, "low_sentiment"),
            Fragment("Entendo como isso pode ser desgastante", ABERTURA, "low_sentiment"),
            Fragment("Parece que tem uma carga emocional aí", ABERTURA, "low_sentiment"),
            Fragment("Isso que você trouxe parece vir de um lugar meio pesado", ABERTURA, "low_sentiment")
        ),
        "high_sentiment" to listOf(
            Fragment("Que bom te ver assim", ABERTURA, "high_sentiment"),
            Fragment("Tô sentindo um astral legal vindo de você hoje", ABERTURA, "high_sentiment"),
            Fragment("É tão bom quando você tá assim", ABERTURA, "high_sentiment"),
            Fragment("Que bacana te ver desse jeito", ABERTURA, "high_sentiment"),
            Fragment("Sua energia hoje tá boa demais", ABERTURA, "high_sentiment")
        ),
        "low_momentum" to listOf(
            Fragment("Estou aqui", ABERTURA, "low_momentum"),
            Fragment("Tô aqui, presente", ABERTURA, "low_momentum"),
            Fragment("Estou por aqui", ABERTURA, "low_momentum"),
            Fragment("Sem pressa", ABERTURA, "low_momentum"),
            Fragment("Calma, tô aqui", ABERTURA, "low_momentum"),
            Fragment("Não precisa ter pressa", ABERTURA, "low_momentum")
        ),
        "centrality" to listOf(
            Fragment("Esse assunto parece ocupar um lugar importante no que você pensa", ABERTURA, "centrality", true),
            Fragment("Esse tema tem raízes fundas no que você traz", ABERTURA, "centrality", true),
            Fragment("Essa ideia volta sempre", ABERTURA, "centrality", true),
            Fragment("Isso que você trouxe agora ecoa muito aqui dentro", ABERTURA, "centrality", true),
            Fragment("Esse é um daqueles temas que realmente te acompanham", ABERTURA, "centrality", true),
            Fragment("É curioso como esse tema reaparece", ABERTURA, "centrality", true),
            Fragment("Sinto que esse não é um assunto qualquer pra você", ABERTURA, "centrality", true)
        )
    )

    val conectores: Map<String, List<Fragment>> = mapOf(
        "default" to listOf(
            Fragment(" — ", CONECTOR, "default"),
            Fragment(". ", CONECTOR, "default"),
            Fragment("… ", CONECTOR, "default"),
            Fragment(": ", CONECTOR, "default"),
            Fragment(", ", CONECTOR, "default"),
            Fragment(". ", CONECTOR, "default")
        ),
        "gentle" to listOf(
            Fragment(" — acho que ", CONECTOR, "gentle"),
            Fragment(", porque ", CONECTOR, "gentle"),
            Fragment(", já que ", CONECTOR, "gentle"),
            Fragment(" — o que me leva a pensar que ", CONECTOR, "gentle"),
            Fragment(", então ", CONECTOR, "gentle")
        ),
        "question_tail" to listOf(
            Fragment(". Deixe-me pensar com calma", CONECTOR, "question_tail"),
            Fragment(". Vou refletir sobre isso", CONECTOR, "question_tail"),
            Fragment(". Vou processar isso com calma", CONECTOR, "question_tail"),
            Fragment(". Vou pensar com carinho", CONECTOR, "question_tail"),
            Fragment(". Merece uma reflexão à altura", CONECTOR, "question_tail"),
            Fragment(". Vou digerir isso", CONECTOR, "question_tail")
        ),
        "continuation" to listOf(
            Fragment(". Continua", CONECTOR, "continuation"),
            Fragment(". Tô acompanhando", CONECTOR, "continuation"),
            Fragment(". Pode continuar", CONECTOR, "continuation"),
            Fragment(". Siga em frente", CONECTOR, "continuation"),
            Fragment(". Manda mais", CONECTOR, "continuation"),
            Fragment(". Pode seguir", CONECTOR, "continuation")
        ),
        "companion" to listOf(
            Fragment(". Estou aqui", CONECTOR, "companion"),
            Fragment(". Tô aqui pra te ouvir", CONECTOR, "companion"),
            Fragment(". Pode falar, tô contigo", CONECTOR, "companion"),
            Fragment(". Você não tá sozinho nessa reflexão", CONECTOR, "companion"),
            Fragment(". Tô contigo", CONECTOR, "companion")
        ),
        "follow" to listOf(
            Fragment(". Vamos nessa", CONECTOR, "follow"),
            Fragment(". Pode continuar", CONECTOR, "follow"),
            Fragment(". Vamos juntos", CONECTOR, "follow"),
            Fragment(". Tô junto", CONECTOR, "follow")
        ),
        "but" to listOf(
            Fragment(". Mas ao mesmo tempo ", CONECTOR, "but"),
            Fragment(". Só que agora ", CONECTOR, "but"),
            Fragment(". Enquanto isso ", CONECTOR, "but"),
            Fragment(", mas percebo que ", CONECTOR, "but")
        ),
        "recall" to listOf(
            Fragment(" — isso me fez lembrar de algo que você trouxe antes", CONECTOR, "recall"),
            Fragment(". O que você falou agora conecta com ", CONECTOR, "recall"),
            Fragment(". Isso me puxou uma memória", CONECTOR, "recall")
        ),
        "recall_tail" to listOf(
            Fragment("aquela outra vez que você disse sobre \$snippet", CONECTOR, "recall_tail"),
            Fragment("quando você falou de \$snippet", CONECTOR, "recall_tail"),
            Fragment("\$snippet", CONECTOR, "recall_tail"),
            Fragment("você já tinha tocado nesse ponto quando falou de \$snippet", CONECTOR, "recall_tail")
        ),
        "tag_ref" to listOf(
            Fragment("— \$tagNote volta com frequência", CONECTOR, "tag_ref"),
            Fragment(". \$tagNote realmente te acompanha", CONECTOR, "tag_ref"),
            Fragment(". Parece ser um tema que volta sempre", CONECTOR, "tag_ref"),
            Fragment(" — já falamos sobre \$tagNote antes", CONECTOR, "tag_ref"),
            Fragment(" — especialmente \$tagNote", CONECTOR, "tag_ref"),
            Fragment(" — \$tagNote veio antes também", CONECTOR, "tag_ref"),
            Fragment("! Esse tópico realmente te acompanha", CONECTOR, "tag_ref")
        ),
        "drift_note" to listOf(
            Fragment(" — e faz sentido ir por aqui também", CONECTOR, "drift_note"),
            Fragment(". Tudo bem, estou aqui", CONECTOR, "drift_note"),
            Fragment(". Vamos juntos", CONECTOR, "drift_note"),
            Fragment(". Sem problema", CONECTOR, "drift_note"),
            Fragment(". E tá tudo bem", CONECTOR, "drift_note"),
            Fragment(". Faz parte", CONECTOR, "drift_note")
        ),
        "no_connector" to listOf(
            Fragment("", CONECTOR, "no_connector"),
            Fragment("", CONECTOR, "no_connector"),
            Fragment("", CONECTOR, "no_connector")
        )
    )

    val fechos: Map<String, List<Fragment>> = mapOf(
        "default" to listOf(
            Fragment("", FECHO, "default"),
            Fragment("", FECHO, "default"),
            Fragment(".", FECHO, "default"),
            Fragment("…", FECHO, "default"),
            Fragment("!", FECHO, "default")
        ),
        "question" to listOf(
            Fragment("", FECHO, "question"),
            Fragment(", não é?", FECHO, "question"),
            Fragment(", certo?", FECHO, "question"),
            Fragment(", né?", FECHO, "question")
        ),
        "invite" to listOf(
            Fragment("", FECHO, "invite"),
            Fragment(" O que você acha?", FECHO, "invite"),
            Fragment(" Como você se sente sobre isso?", FECHO, "invite"),
            Fragment(" Me conta mais sobre isso", FECHO, "invite"),
            Fragment(" O que está pensando agora?", FECHO, "invite")
        ),
        "prompt" to listOf(
            Fragment(". O que você está pensando?", FECHO, "prompt"),
            Fragment(". Pode falar o que vier", FECHO, "prompt"),
            Fragment(". Quando quiser falar algo, é só dizer", FECHO, "prompt"),
            Fragment(". O que vier à mente", FECHO, "prompt"),
            Fragment(". Pode desabafar à vontade", FECHO, "prompt"),
            Fragment(". O que você quiser compartilhar", FECHO, "prompt"),
            Fragment(". Manda aí quando sentir vontade", FECHO, "prompt"),
            Fragment(". Se quiser falar algo, tô aqui", FECHO, "prompt")
        ),
        "explain" to listOf(
            Fragment(". Faz parte, pensamentos evoluem", FECHO, "explain"),
            Fragment(". Gosto disso", FECHO, "explain"),
            Fragment(". É natural", FECHO, "explain"),
            Fragment(". Mas tudo bem", FECHO, "explain"),
            Fragment(". E isso é bom", FECHO, "explain")
        ),
        "depth" to listOf(
            Fragment(". Há camadas aqui que valem a pena explorar", FECHO, "depth"),
            Fragment(". Você está realmente mergulhando fundo nessa ideia", FECHO, "depth"),
            Fragment(". É um pensamento que tem peso", FECHO, "depth"),
            Fragment(". Tem muita coisa aqui pra desdobrar", FECHO, "depth"),
            Fragment(". Isso merece atenção", FECHO, "depth"),
            Fragment(". Parece que você vem matutando isso há um tempo", FECHO, "depth")
        ),
        "time" to listOf(
            Fragment(" antes", FECHO, "time"),
            Fragment(" em outra conversa", FECHO, "time"),
            Fragment(" da última vez", FECHO, "time"),
            Fragment(" outro dia", FECHO, "time"),
            Fragment(" há um tempo", FECHO, "time"),
            Fragment(" antes também", FECHO, "time")
        ),
        "revisit" to listOf(
            Fragment(", como se revisitando uma ideia antiga enquanto explora outra", FECHO, "revisit"),
            Fragment(". Curioso como essa ideia volta enquanto o pensamento se move", FECHO, "revisit"),
            Fragment(", mas parece que você tá olhando de um ângulo diferente agora", FECHO, "revisit"),
            Fragment(". Só que agora sinto que você quer levar pra outro lugar", FECHO, "revisit"),
            Fragment(", mas ao mesmo tempo tem algo novo se formando", FECHO, "revisit")
        ),
        "contradiction_note" to listOf(
            Fragment(" — sinal de reflexão", FECHO, "contradiction_note"),
            Fragment(". A gente muda de ideia", FECHO, "contradiction_note"),
            Fragment(", mas talvez seja uma evolução natural", FECHO, "contradiction_note"),
            Fragment(" — pensar é revisitar", FECHO, "contradiction_note")
        ),
        "warm" to listOf(
            Fragment(" :)", FECHO, "warm"),
            Fragment("!", FECHO, "warm"),
            Fragment("!", FECHO, "warm"),
            Fragment(" — tô gostando de ver", FECHO, "warm"),
            Fragment("!", FECHO, "warm"),
            Fragment("!", FECHO, "warm")
        ),
        "denial_note" to listOf(
            Fragment(". Revisitar uma ideia faz parte do processo", FECHO, "denial_note"),
            Fragment(". Isso também é evolução", FECHO, "denial_note"),
            Fragment(". Nem tudo precisa ser definitivo", FECHO, "denial_note"),
            Fragment(". A gente se entende melhor revisando mesmo", FECHO, "denial_note"),
            Fragment(". Faz parte do processo", FECHO, "denial_note")
        ),
        "denial_warm_note" to listOf(
            Fragment(". Não precisa se explicar", FECHO, "denial_warm_note"),
            Fragment(" — entendo completamente", FECHO, "denial_warm_note"),
            Fragment(". Entendo seu lado", FECHO, "denial_warm_note"),
            Fragment(". Eu entendo", FECHO, "denial_warm_note")
        ),
        "affirmation_note" to listOf(
            Fragment(". Vamos em frente então", FECHO, "affirmation_note"),
            Fragment(". Pode seguir", FECHO, "affirmation_note"),
            Fragment(". Tô junto", FECHO, "affirmation_note"),
            Fragment(". Vamos nessa", FECHO, "affirmation_note"),
            Fragment(". Pode continuar", FECHO, "affirmation_note")
        ),
        "momentum_note" to listOf(
            Fragment(". Pode vir no seu ritmo", FECHO, "momentum_note"),
            Fragment(". O espaço é seu, no seu tempo", FECHO, "momentum_note"),
            Fragment(". Tô aqui exatamente onde você está", FECHO, "momentum_note"),
            Fragment(". O tempo é seu", FECHO, "momentum_note")
        ),
        "open_question" to listOf(
            Fragment(". Me conta, o que te traz por aqui hoje?", FECHO, "open_question"),
            Fragment(". O que você está pensando nesse momento?", FECHO, "open_question"),
            Fragment(". Tem algo em mente?", FECHO, "open_question"),
            Fragment(". Como está sendo seu dia?", FECHO, "open_question"),
            Fragment(". O que você andou pensando ultimamente?", FECHO, "open_question"),
            Fragment(". Me conta um pouco sobre o que passa pela sua cabeça agora", FECHO, "open_question"),
            Fragment(". Tem algo que você guardou pra compartilhar?", FECHO, "open_question"),
            Fragment(". Por onde você quer começar?", FECHO, "open_question")
        ),
        "state_question" to listOf(
            Fragment(". Como estão as coisas?", FECHO, "state_question"),
            Fragment(". O que está passando pela sua mente hoje?", FECHO, "state_question"),
            Fragment(". O que você traz para conversarmos?", FECHO, "state_question"),
            Fragment(". Tudo bem por aí?", FECHO, "state_question"),
            Fragment(". O que anda acontecendo?", FECHO, "state_question"),
            Fragment(". Como você está hoje?", FECHO, "state_question"),
            Fragment(". Me conta as novidades", FECHO, "state_question"),
            Fragment(". O que você tem em mente?", FECHO, "state_question")
        ),
        "warm_check" to listOf(
            Fragment("! Parece que você tá num dia bom", FECHO, "warm_check"),
            Fragment("! Tô sentindo uma energia boa vinda de você hoje", FECHO, "warm_check"),
            Fragment("! E com esse astral todo ainda", FECHO, "warm_check"),
            Fragment("! E parece que o dia tá favorável", FECHO, "warm_check"),
            Fragment("! Tô percebendo que você tá bem hoje", FECHO, "warm_check")
        ),
        "thinking" to listOf(
            Fragment(". Deixe-me pensar junto com você", FECHO, "thinking"),
            Fragment(". Vou refletir sobre isso", FECHO, "thinking"),
            Fragment(". Deixe-me ver o que encontro sobre isso", FECHO, "thinking"),
            Fragment(". Vou processar isso com calma", FECHO, "thinking"),
            Fragment(". Deixa eu pensar um pouco", FECHO, "thinking"),
            Fragment(". Deixa eu ver o que vem à mente", FECHO, "thinking"),
            Fragment(". Vou refletir", FECHO, "thinking"),
            Fragment(". Vou mastigar essa ideia um pouco", FECHO, "thinking")
        )
    )
}
