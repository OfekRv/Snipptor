import { IEngine } from 'app/shared/model/engine.model';
import { IVulnerability } from 'app/shared/model/vulnerability.model';
import { ISnippetMatchedRules } from 'app/shared/model/snippet-matched-rules.model';

export interface IRule {
  id?: number;
  name?: string;
  raw?: string | null;
  engine?: IEngine | null;
  vulnerability?: IVulnerability | null;
  snippetMatchedRules?: ISnippetMatchedRules[] | null;
}

export const defaultValue: Readonly<IRule> = {};
