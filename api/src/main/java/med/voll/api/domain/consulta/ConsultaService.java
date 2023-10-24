package med.voll.api.domain.consulta;

import med.voll.api.domain.consulta.validacoes.ValidadorAgendamentoConsultas;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private List<ValidadorAgendamentoConsultas> validadores;

    public DadosDetalhamentoConsulta agendar(DadosAgendamentoConsulta dados){
        if(!pacienteRepository.existsById(dados.idPaciente())){
            throw new ValidacaoException("Id do paciente não existe!");
        }
        if(dados.idMedico()!=null && !medicoRepository.existsById(dados.idMedico())){
            throw new ValidacaoException("Id do médico não existe!");
        }

        //injetando todos os validadores que extendem da interface ValidadorAgendamentoConsultas
        validadores.forEach(v-> v.validar(dados));

        var paciente = pacienteRepository.findById(dados.idPaciente()).get();
        var medico = escolhermedico(dados);
        if(medico==null){
            throw new ValidacaoException("Nao existe médico disponível nesta data");
        }
        var consulta= new Consulta(null, medico, paciente,dados.data());
        consultaRepository.save(consulta);

        return new DadosDetalhamentoConsulta(consulta);
    }

    private Medico escolhermedico(DadosAgendamentoConsulta dados) {
        if(dados.idMedico()!=null){
            return medicoRepository.getReferenceById(dados.idMedico());
        }
        if(dados.especialidade()==null){
            throw new ValidacaoException("Especialidade é obrigatória quando médico não for escolhido!");
        }

        return medicoRepository.escolherMedicoAleatorioDisponivelNaData(dados.especialidade(),dados.data());

    }

}
