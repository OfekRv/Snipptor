import { IRule } from 'app/shared/model/rule.model';

export interface ISnippetMatchedRules {
  id?: number;
  rules?: IRule[] | null;
}

export const defaultValue: Readonly<ISnippetMatchedRules> = {};
